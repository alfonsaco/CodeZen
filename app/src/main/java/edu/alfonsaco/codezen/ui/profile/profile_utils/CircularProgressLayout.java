package edu.alfonsaco.codezen.ui.profile.profile_utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;

import edu.alfonsaco.codezen.R;

// FORMA DEL PROGRESS BAR DEL NIVEL DEL USUARIO
public class CircularProgressLayout extends LinearLayout {
    private Path clipPath;
    private Paint borderPaint;
    private RectF boundsRect;
    private float borderWidth;
    private int borderColor;

    public CircularProgressLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CircularProgressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        clipPath = new Path();
        boundsRect = new RectF();

        // Valores por defecto
        borderWidth = 2f;

        // Obtenemos el color ColorPrimary
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        borderColor = color;

        // Obtener atributos personalizados si los hay
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressLayout);
            borderWidth = a.getDimension(R.styleable.CircularProgressLayout_borderWidth, borderWidth);
            borderColor = a.getColor(R.styleable.CircularProgressLayout_borderColor, borderColor);
            a.recycle();
        }

        // Convertir dp a px
        borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth,
                getResources().getDisplayMetrics());

        // Configurar el paint para el borde
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);

        // Importante para que el borde no se recorte
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Ajustar el rectángulo de bordes considerando el ancho del borde
        boundsRect.set(borderWidth/2, borderWidth/2,
                w - borderWidth/2, h - borderWidth/2);

        clipPath.reset();
        float radius = Math.min(w, h) / 2f;
        clipPath.addRoundRect(boundsRect, radius, radius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Primero dibujar el contenido recortado
        canvas.save();
        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);
        canvas.restore();

        // Luego dibujar el borde
        canvas.drawRoundRect(boundsRect, boundsRect.height()/2, boundsRect.height()/2, borderPaint);
    }
}