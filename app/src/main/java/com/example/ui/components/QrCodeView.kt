package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    qrColor: Color = Color.White,
    backgroundColor: Color = Color(0xFF13151B)
) {
    // Generate deterministic pattern based on room PIN
    val matrix = remember(data) {
        val gridSize = 21
        val grid = Array(gridSize) { BooleanArray(gridSize) }
        val random = Random(data.hashCode())

        // Fill background noise
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                grid[r][c] = random.nextFloat() > 0.45f
            }
        }

        // Apply 3 standard QR finder patterns in corners (top-left, top-right, bottom-left)
        fun applyFinder(startR: Int, startC: Int) {
            for (r in 0..6) {
                for (c in 0..6) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    grid[startR + r][startC + c] = isBorder || isCenter
                }
            }
            // Clear quiet zone around finder
            for (r in -1..7) {
                for (c in -1..7) {
                    val targetR = startR + r
                    val targetC = startC + c
                    if (targetR in 0 until gridSize && targetC in 0 until gridSize) {
                        if (r == -1 || r == 7 || c == -1 || c == 7) {
                            grid[targetR][targetC] = false
                        }
                    }
                }
            }
        }

        applyFinder(0, 0)
        applyFinder(0, gridSize - 7)
        applyFinder(gridSize - 7, 0)

        grid
    }

    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(1.5.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val gridSize = matrix.size
            val cellSize = this.size.width / gridSize
            val dotRadius = cellSize * 0.35f

            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    if (matrix[r][c]) {
                        drawRoundRect(
                            color = qrColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 0.9f, cellSize * 0.9f),
                            cornerRadius = CornerRadius(dotRadius, dotRadius)
                        )
                    }
                }
            }
        }
    }
}
