package com.jetpack.canvas

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.canvas.ui.theme.CanvasTheme

import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanvasTheme {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Circle(
                            dotColor = listOf(Color.Transparent, Color.Gray),
                            inactiveColor = Color.LightGray,
                            activeColor = listOf(Color.Red, Color.Yellow, Color.Green),
                            modifier = Modifier.size(300.dp),
                            strokeWidth = 30f
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Circle(
    dotColor: List<Color>,
    inactiveColor: Color,
    activeColor: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Float

) {
    var angle by remember { mutableStateOf(0f) }
    var dragAngle by remember { mutableStateOf(0f) }
    var oldAngle by remember { mutableStateOf(angle) }
    val offsetAngleDegree = 0f

    Canvas(modifier = modifier
        .padding(30.dp)
        .pointerInput(true) {
            detectDragGestures(
                onDragStart = { offset ->
                    dragAngle = atan2(
                        y = size.center.x - offset.x,
                        x = size.center.y - offset.y
                    ) * (180f / Math.PI.toFloat()) * -1
                },
                onDragEnd = {
                    oldAngle = angle
                }
            ) { change, _ ->

                val touchAngle = atan2(
                    y = size.center.x - change.position.x,
                    x = size.center.y - change.position.y
                ) * (180f / Math.PI.toFloat()) * -1

                angle = oldAngle + (touchAngle - dragAngle)
                if (angle > 360) {
                    angle -= 360
                } else if (angle < 0) {
                    angle = 360 - abs(angle)
                }

                if (angle > 360f - (offsetAngleDegree * .8f))
                    angle = 0f
                else if (angle > 0f && angle < offsetAngleDegree)
                    angle = offsetAngleDegree

            }
        }

    ) {

        val radius = size.width * .5f
        val circularDot = Offset(
            x = (radius) * cos((angle - 270f) * (Math.PI / 180f).toFloat()) + size.center.x,
            y = (radius) * sin((angle - 270f) * (Math.PI / 180f).toFloat()) + size.center.y
        )
        drawCircle(
            color = inactiveColor,
            radius,
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
        )

        drawArc(
            useCenter = false,
            startAngle = -270f,
            sweepAngle = round(angle),
            brush = Brush.horizontalGradient(colors = activeColor),
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = dotColor,
                center = circularDot,
                radius = radius * (1f / 9f)
            ),
            radius = radius * (1f / 9f),
            center = circularDot
        )

        drawIntoCanvas {
            val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint()
            paint.apply {
                isAntiAlias = true
                textSize = 30.sp.toPx()
                color = dotColor.hashCode()
                textAlign = Paint.Align.CENTER
            }
            it.nativeCanvas.drawText(
                "${round(angle).toInt()}°",
                size.width / 2,
                size.height / 2,
                paint
            )
        }

    }
}