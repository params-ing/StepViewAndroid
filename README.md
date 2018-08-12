# StepViewAndroid
An android library written in kotlin to display steps (without any max-min limits) along with the descriptions. It also supports some really cool features.

## Usage
Here is a basic example of a StepView

![alt Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/basic_stepview.png)

```
<params.com.statusView.StatusViewScroller
  android:id="@+id/statusView"
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  app:circleColor="#F1EA7F"
  app:circleRadius="20dp"
  app:complete_drawable="@drawable/ic_done_black_24dp"
  app:statusCount="4"
  app:currentCount="4"
  app:drawCount="true"
  android:entries="@array/statuses" />
```            
![alt Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/colors_stepview.png)

```
<params.com.statusView.StatusViewScroller
    android:id="@+id/statusView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:circleColorType="fillStroke"
    app:lineGap="5dp"
    app:statusCount="5"
    app:currentCount="4"
    app:drawCount="true"
    android:entries="@array/statuses"
    app:lineWidth="3dp"
    app:lineColorIncomplete="#B4B7BA"
    app:lineColor="@android:color/black"
    app:lineColorCurrent="#91A8D0"
    app:circleColor="#CC004B8D"
    app:circleColorIncomplete="#BCBCBE"
    app:circleColorCurrent="#004B8D"
    app:textColorLabels="@android:color/white"
    app:textColorLabelsCurrent="@android:color/white"
    app:textColorLabelsIncomplete="@android:color/black"
    app:circleStrokeWidth="4dp"
    app:circleStrokeColorCurrent="#91A8D0"
    app:complete_drawable="@drawable/ic_done_black_24dp" />
```
You can set colors