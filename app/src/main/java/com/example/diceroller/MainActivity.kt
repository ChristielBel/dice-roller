package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diceroller.ui.theme.DiceRollerTheme
import com.example.diceroller.ui.theme.lightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollerTheme {
                DiceRollerApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiceRollerApp() {
    DiceWithButtonAndImage(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

val BounceInterpolatorEasing = Easing { fraction ->
    val t = fraction
    if (t < 0.3636f) 7.5625f * t * t
    else if (t < 0.7272f) {
        val t2 = t - 0.5454f
        7.5625f * t2 * t2 + 0.75f
    } else if (t < 0.9090f) {
        val t2 = t - 0.8181f
        7.5625f * t2 * t2 + 0.9375f
    } else {
        val t2 = t - 0.9545f
        7.5625f * t2 * t2 + 0.984375f
    }
}

@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    var result by remember { mutableStateOf(1) }
    var isAnimating by remember { mutableStateOf(false) }

    val rotation = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageResource = when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        Image(
            painter = painterResource(imageResource),
            contentDescription = "dice $result",
            modifier = Modifier
                .graphicsLayer(
                    translationX = offsetX.value,
                    translationY = offsetY.value,
                    scaleX = scale.value,
                    scaleY = scale.value,
                    rotationZ = rotation.value
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isAnimating = true
                scope.launch {
                    launch {
                        rotation.animateTo(
                            targetValue = rotation.value + 1080f,
                            animationSpec = tween(1000, easing = FastOutSlowInEasing)
                        )
                    }
                    launch {
                        scale.animateTo(1.2f, tween(100, easing = LinearOutSlowInEasing))
                        scale.animateTo(1f, tween(150))
                    }
                    launch {
                        repeat(6) {
                            offsetX.animateTo(
                                if (it % 2 == 0) 20f else -20f,
                                tween(50)
                            )
                        }
                        offsetX.animateTo(0f)
                    }
                    launch {
                        offsetY.animateTo(-30f, tween(150, easing = FastOutLinearInEasing))
                        offsetY.animateTo(0f, tween(250, easing = BounceInterpolatorEasing))
                    }

                    delay(900)
                    result = (1..6).random()
                    isAnimating = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
            enabled = !isAnimating
        ) {
            Text(stringResource(R.string.roll))

        }
    }
}