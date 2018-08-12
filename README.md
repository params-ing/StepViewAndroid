# StepViewAndroid
An android library written in kotlin to display steps (without any max-min limits) along with the descriptions. It also supports some really cool features.

## Usage

### A customised StepView

![Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/colors_stepview.png)

* Set total step count and current count.

* Set different dimensions for stroke width, line width, text size of label (step count) & line Gap.

* Stroke color (circle), fill color (circle), line color & label text color can be set differently for completed, current & incomplete states.

```XML
<params.com.statusView.StatusViewScroller
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:statusCount="5"
    app:currentCount="4"
    app:drawCount="true"
    android:entries="@array/statuses"
    app:circleColorType="fillStroke"
    app:lineGap="5dp"
    app:lineWidth="3dp"
    app:lineColor="@android:color/black"
    app:lineColorIncomplete="#B4B7BA"
    app:lineColorCurrent="#91A8D0"
    app:circleColor="#CC004B8D"
    app:circleColorCurrent="#004B8D"
    app:circleColorIncomplete="#BCBCBE"
    app:textColorLabels="@android:color/white"
    app:textColorLabelsCurrent="@android:color/white"
    app:textColorLabelsIncomplete="@android:color/black"
    app:circleStrokeWidth="4dp"
    app:circleStrokeColorCurrent="#91A8D0"
    app:textSizeLabels="15sp"
    app:complete_drawable="@drawable/ic_done_black_24dp" />
```

### Some more customisations

![Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/drawable_zoom_stepview.png)

* Set a zoom value to your current step
* Set different drawables for completed, current & incomplete states.

```XML
    app:currentStatusZoom="0.5"
    app:complete_drawable="@drawable/ic_satisfied_black_24dp"
    app:current_drawable="@drawable/ic_dissatisfied_black_24dp"
    app:incomplete_drawable="@drawable/ic_very_dissatisfied_black_24dp"
```

### Step description customisations

![Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/android_stepview.png)

* Multiline Text Support: View automatically adjusts line length to make sure words or alphabets from one line do not crossover to next line.
  However, If you want to strictly obey line length set ```app:strictObeyLineLength= "true"```.
* To add font to description simply refer to the font file from res/font directory.
* Ensure a minimum margin between description texts in case they are too close.
* Set top margin of a status description from its corresponding circle.
* Align all descriptions at the same level in case, a zoom level is set to current status.
* Set text color to status descriptions.

```XML
    app:currentStatusZoom="0.3"
    app:statusTextFont="@font/lekton_r"
    app:statusTextTopMargin="15dp"
    app:minStatusTextAdjacentMargin="5dp"
    app:alignStatusTextWithCurrent="true"
    app:textColorStatusText="#000000"
```

### Scrolling property

![Downloading..](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/fragment_scroll_stepview.gif)

* The view can be scrolled if the content exceeds the available width.
* You can also scroll to a particular step by calling ```statusViewScroller.scrollToPos(stepCount)```
