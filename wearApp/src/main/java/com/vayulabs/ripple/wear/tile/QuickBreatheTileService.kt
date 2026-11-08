package com.vayulabs.ripple.wear.tile

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.LayoutElementBuilders.*
import androidx.wear.tiles.ModifiersBuilders.*
import com.vayulabs.ripple.wear.presentation.WearMainActivity
import com.google.common.util.concurrent.ListenableFuture

class QuickBreatheTileService : TileService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val launchIntent = Intent(this, WearMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val layout = Layout.Builder()
            .setRoot(
                Box.Builder()
                    .setWidth(DimensionBuilders.expand())
                    .setHeight(DimensionBuilders.expand())
                    .setModifiers(
                        Modifiers.Builder()
                            .setClickable(
                                Clickable.Builder()
                                    .setOnClick(ActionBuilders.LaunchAction.Builder().setAndroidActivity(
                                        ActionBuilders.AndroidActivity.Builder()
                                            .setPackageName(packageName)
                                            .setClassName(WearMainActivity::class.java.name)
                                            .build()
                                    ).build())
                                    .build()
                            )
                            .setBackground(
                                Background.Builder()
                                    .setColor(ColorBuilders.argb(0xFF0D1117.toInt()))
                                    .build()
                            )
                            .build()
                    )
                    .addContent(
                        Column.Builder()
                            .setWidth(DimensionBuilders.wrap())
                            .setHeight(DimensionBuilders.wrap())
                            .addContent(
                                Text.Builder()
                                    .setText("Ripple")
                                    .setFontStyle(
                                        FontStyle.Builder()
                                            .setSize(DimensionBuilders.sp(14f))
                                            .setColor(ColorBuilders.argb(0xFF648BA2.toInt()))
                                            .build()
                                    )
                                    .build()
                            )
                            .addContent(
                                Spacer.Builder().setHeight(dp(8f)).build()
                            )
                            .addContent(
                                Text.Builder()
                                    .setText("TAP TO BREATHE")
                                    .setFontStyle(
                                        FontStyle.Builder()
                                            .setSize(DimensionBuilders.sp(16f))
                                            .setColor(ColorBuilders.argb(0xFFE6EDF3.toInt()))
                                            .build()
                                    )
                                    .build()
                            )
                            .addContent(
                                Spacer.Builder().setHeight(dp(4f)).build()
                            )
                            .addContent(
                                Text.Builder()
                                    .setText("Silent Wrist Haptics")
                                    .setFontStyle(
                                        FontStyle.Builder()
                                            .setSize(DimensionBuilders.sp(11f))
                                            .setColor(ColorBuilders.argb(0xFF8B949E.toInt()))
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        val timeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(layout)
                    .build()
            )
            .build()

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTimeline(timeline)
            .build()

        val future = androidx.concurrent.futures.ResolvableFuture.create<TileBuilders.Tile>()
        future.set(tile)
        return future
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<androidx.wear.protolayout.ResourceBuilders.Resources> {
        val future = androidx.concurrent.futures.ResolvableFuture.create<androidx.wear.protolayout.ResourceBuilders.Resources>()
        val resources = androidx.wear.protolayout.ResourceBuilders.Resources.Builder()
            .setVersion("1")
            .build()
        future.set(resources)
        return future
    }
}
