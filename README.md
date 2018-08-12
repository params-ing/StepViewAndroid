# StepViewAndroid
An android library written in kotlin to display steps (without any max-min limits) along with the descriptions. It also supports some really cool features.

## Usage
Here is a basic example of a StepView

![alt oops](https://github.com/params-ing/StepViewAndroid/blob/dev/screenshots/basic_stepview.png)

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
            
           
