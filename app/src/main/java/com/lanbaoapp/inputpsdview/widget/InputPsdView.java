package com.lanbaoapp.inputpsdview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

/**
 * Created by lx on 2017/8/29.
 */
public class InputPsdView extends View {

    private InputMethodManager input;//输入法管理

    private Paint mPaint;
    private Paint mPaintText;
    //设置输入框的个数 默认是6个
    private int mCount = 6;
    //间距
    private int offX = 30;
    //默认一个格子的宽度
    private int mItemWidth = 100;
    //圆角大小 默认是10
    private int mFilletSize = 15;

    private int mStrokeWidth = 2;

    //输入明文还是点 0 明文 1点
    private int mType = 1;
    private ArrayList<Integer> result;//输入结果保存

    public InputPsdView(Context context) {
        this(context,null);
    }

    public InputPsdView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public InputPsdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {

        result=new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(mStrokeWidth);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(45);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        input=(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.setOnKeyListener(new MyKeyListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode != MeasureSpec.EXACTLY) { //如果控件没有写死宽度和高度
            width = mCount * (mItemWidth + offX) + 20;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mItemWidth + 20;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //循环要绘制密码框的个数
        for (int i = 0; i < mCount; i++) {
            mPaint.setColor(Color.RED);
            //确定每个密码的框的位置 offX是间距
            RectF rectF = new RectF();
            rectF.left = i * (mItemWidth + offX) + 10;
            rectF.top = 10;
            rectF.right = mItemWidth + (i * (mItemWidth + offX));
            rectF.bottom = mItemWidth;
            //绘制密码框
            canvas.drawRoundRect(rectF,mFilletSize,mFilletSize,mPaint);
        }
        // 明文和密文的切换
        for (int i = 0; i < result.size(); i++) {
            //判断是明文还是密文
            if (mType == 0) {
                //明文输入
                canvas.drawText(String.valueOf(result.get(i)) ,
                        i * (mItemWidth + offX) + mItemWidth / 2 + 5,mItemWidth / 2 + mItemWidth / 5 ,mPaintText);
            }else{
                //密文输入
                canvas.drawCircle(i * (mItemWidth + offX) + mItemWidth / 2 + 5
                        ,mItemWidth / 2 + 5 ,15,mPaintText);
            }
        }
        //循环存放输入的字符的list集合,把后一个没有输入的框标记为待输入的颜色
        if (result.size() < mCount) {
            mPaint.setColor(Color.BLUE);
            RectF rectF = new RectF();
            rectF.left = result.size() * (mItemWidth + offX) + 10;
            rectF.top = 10;
            rectF.right = mItemWidth + (result.size() * (mItemWidth + offX));
            rectF.bottom = mItemWidth;
            canvas.drawRoundRect(rectF,mFilletSize,mFilletSize,mPaint);
        }

    }

    //重写onTouchEvent事件,在用户按下唤起软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()== MotionEvent.ACTION_DOWN){//点击控件弹出输入键盘
            requestFocus();
            input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            return true;
        }
        return super.onTouchEvent(event);
    }


    //监听软键盘的输入
    class MyKeyListener implements OnKeyListener{

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {

                if (event.isShiftPressed()) { // 处理*#键
                    return false;
                }
                // 把输入的数字添加到存放数字的集合中
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    if (result.size() < mCount) {
                        result.add(keyCode - 7);
                        invalidate();
                        ensureFinishInput();

                    }
                    return true;
                }
                //监听删除键
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!result.isEmpty()) { // 不为空,删除最后一个
                        result.remove(result.size() - 1);
                        invalidate();
                    }
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
            }
            return false;
        }

        void ensureFinishInput(){
            if (result.size() == mCount && mOnInputFinishListener != null) {
                StringBuffer sb = new StringBuffer();
                for (Integer integer : result) {
                    sb.append(integer + "");
                }
                mOnInputFinishListener.inputFinish(sb.toString());
            }
        }
    }

    //设置键盘的输入类型
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new MyInputConnection(this,false);
    }

    class MyInputConnection extends BaseInputConnection {
        public MyInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            //这里是接受输入法的文本的，我们只处理数字，所以什么操作都不做
            return super.commitText(text,newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            //软键盘的删除键 DEL 无法直接监听，自己发送del事件
            if (beforeLength==1 && afterLength==0){
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }


    //输入完成的回调
    OnInputFinishListener mOnInputFinishListener;
    public interface OnInputFinishListener{
        void inputFinish(String str);
    }

    public void setOnInputFinishListener(OnInputFinishListener onInputFinishListener){
        mOnInputFinishListener = onInputFinishListener;
    }

    public void setExpress(int type){
        mType = type;
        invalidate();
    }

    //明文密文的切换
    public boolean getIsExpress(){
        if (mType == 0){
            return true;
        }else{
            return false;
        }
    }
    //每一个输入框的宽度
    public void setItemWidth(int itemWidth){
        mItemWidth = itemWidth;
        invalidate();
    }

    //输入框的圆角
    public void setFillet(int fillet){
        mFilletSize = fillet;
        invalidate();
    }



}
