package vn.edu.tdc.cddd2.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import vn.edu.tdc.cddd2.R;

public class InDecreaseViewControl extends ConstraintLayout {
    // Khai báo biến:
    private ViewGroup inDecreaseLayout;

    public InDecreaseViewControl(Context context) {
        super(context);
        init();
    }

    public InDecreaseViewControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InDecreaseViewControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        ViewGroup viewGroup = (ViewGroup) inflate(getContext(), R.layout.item_indecrease_button, this);
        inDecreaseLayout = (ViewGroup) viewGroup.getChildAt(0);
    }
}
