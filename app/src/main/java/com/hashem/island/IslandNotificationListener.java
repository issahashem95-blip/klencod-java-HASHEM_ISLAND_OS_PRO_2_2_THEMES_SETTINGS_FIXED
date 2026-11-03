package com.hashem.island;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.Intent;
import android.app.Notification;
import android.os.Bundle;

/** Notification access: notification text is processed locally and is not uploaded. */
public class IslandNotificationListener extends NotificationListenerService {
 @Override public void onNotificationPosted(StatusBarNotification sbn) {
  if(sbn==null||sbn.getNotification()==null)return;
  Notification n=sbn.getNotification();
  // Ignore ongoing system foreground-service notifications to avoid flooding the island.
  if((n.flags & Notification.FLAG_ONGOING_EVENT)!=0)return;
  Bundle b=n.extras;
  CharSequence title=b==null?null:b.getCharSequence(Notification.EXTRA_TITLE);
  CharSequence text=b==null?null:b.getCharSequence(Notification.EXTRA_BIG_TEXT);
  if(text==null&&b!=null)text=b.getCharSequence(Notification.EXTRA_TEXT);
  Intent i=new Intent(this,IslandOverlayService.class).setAction("SHOW_EVENT");
  i.putExtra("title",title==null?sbn.getPackageName():title.toString());
  i.putExtra("text",text==null?"إشعار جديد":text.toString());
  try{startService(i);}catch(Exception ignored){}
 }
}
