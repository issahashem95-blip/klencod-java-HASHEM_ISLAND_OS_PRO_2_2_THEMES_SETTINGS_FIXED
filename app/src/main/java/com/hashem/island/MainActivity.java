package com.hashem.island;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.net.Uri;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
 private LinearLayout root; private TextView state;
 private final int gold=Color.rgb(217,180,91);
 private final String[] names={"Gold Pro","Neon Blue","Purple","Emerald","Silver","Pure Black","Red Cyber","Custom"};
 private final int[] colors={0xFFD9B45B,0xFF35C8FF,0xFFB58AFF,0xFF35D69A,0xFFC7D0DA,0xFFEEEEEE,0xFFFF5263,0xFFD9B45B};
 private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(0,9,0,9);return t;}
 private Button btn(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setTextColor(gold);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(27,26,25)));b.setOnClickListener(v->r.run());return b;}
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 @Override public void onCreate(Bundle b){super.onCreate(b);render();}
 private void render(){
  root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(22),dp(26),dp(22),dp(24));root.setBackgroundColor(0xFF08090B);
  ScrollView sc=new ScrollView(this);sc.addView(root);
  TextView brand=label("♛ HASHEM",28,gold);brand.setTypeface(Typeface.DEFAULT_BOLD);root.addView(brand);
  root.addView(label("ISLAND OS PRO • 2.1",12,Color.LTGRAY));
  TextView card=label("HASHEM ISLAND\\nالجزيرة العائمة",18,Color.WHITE);card.setGravity(Gravity.CENTER);card.setBackgroundResource(R.drawable.island_bg);card.setPadding(18,24,18,24);root.addView(card);
  state=label("الحالة: "+(Settings.canDrawOverlays(this)?"إذن الجزيرة متاح":"بحاجة إلى إذن الظهور فوق التطبيقات"),13,Color.GRAY);root.addView(state);
  root.addView(btn("منح إذن الظهور فوق التطبيقات",()->overlayPermission()));
  root.addView(btn("تشغيل الجزيرة العائمة",()->startIsland()));
  root.addView(btn("إيقاف الجزيرة",()->stopService(new Intent(this,IslandOverlayService.class))));
  root.addView(btn("منح الوصول للإشعارات",()->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))));
  root.addView(btn("🎨 الثيمات",()->themes()));
  root.addView(btn("⚙ إعدادات الجزيرة",()->settings()));
  root.addView(btn("حول التطبيق",()->new AlertDialog.Builder(this).setTitle("HASHEM ISLAND OS PRO").setMessage("نسخة 2.1 — إعدادات وثيمات محفوظة محلياً.").setPositiveButton("حسناً",null).show()));
  setContentView(sc);
 }
 private void overlayPermission(){
  if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));startActivity(i);}
  else state.setText("إذن الظهور فوق التطبيقات ممنوح.");
 }
 private void startIsland(){
  if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){overlayPermission();state.setText("امنح الإذن ثم اضغط تشغيل الجزيرة مجدداً.");return;}
  try{if(Build.VERSION.SDK_INT>=26)startForegroundService(new Intent(this,IslandOverlayService.class));else startService(new Intent(this,IslandOverlayService.class));state.setText("تم طلب تشغيل الجزيرة.");}
  catch(Exception e){state.setText("تعذر التشغيل: "+e.getMessage());}
 }
 private void themes(){
  int current=getPreferences(0).getInt("theme",0);
  new AlertDialog.Builder(this).setTitle("اختر الثيم").setSingleChoiceItems(names,current,(d,w)->{
   getPreferences(0).edit().putInt("theme",w).putInt("accent",colors[w]).apply();
   d.dismiss();state.setText("تم تطبيق "+names[w]);sendRefresh();
  }).setNegativeButton("إلغاء",null).show();
 }
 private void settings(){
  LinearLayout panel=new LinearLayout(this);panel.setPadding(dp(20),dp(8),dp(20),dp(8));panel.setOrientation(1);
  TextView preview=label("معاينة الجزيرة • HASHEM",16,Color.WHITE);preview.setGravity(Gravity.CENTER);panel.addView(preview);
  SeekBar size=new SeekBar(this);size.setMax(160);size.setProgress(getPreferences(0).getInt("width",240)-160);panel.addView(label("حجم الجزيرة",14,gold));panel.addView(size);
  SeekBar alpha=new SeekBar(this);alpha.setMax(100);alpha.setProgress(getPreferences(0).getInt("alpha",96));panel.addView(label("شفافية الخلفية",14,gold));panel.addView(alpha);
  SeekBar duration=new SeekBar(this);duration.setMax(10);duration.setProgress(getPreferences(0).getInt("duration",3)-1);panel.addView(label("مدة إشعار الجزيرة (ثوانٍ)",14,gold));panel.addView(duration);
  CheckBox showText=new CheckBox(this);showText.setText("عرض نص الإشعار");showText.setTextColor(Color.WHITE);showText.setChecked(getPreferences(0).getBoolean("showText",true));panel.addView(showText);
  Runnable save=()->{getPreferences(0).edit().putInt("width",160+size.getProgress()).putInt("alpha",alpha.getProgress()).putInt("duration",duration.getProgress()+1).putBoolean("showText",showText.isChecked()).apply();preview.setText("معاينة الجزيرة • "+(160+size.getProgress())+"dp");};
  size.setOnSeekBarChangeListener(listener(save));alpha.setOnSeekBarChangeListener(listener(save));duration.setOnSeekBarChangeListener(listener(save));showText.setOnCheckedChangeListener((b,c)->save.run());
  new AlertDialog.Builder(this).setTitle("إعدادات الجزيرة").setView(panel).setPositiveButton("حفظ وتطبيق",(d,w)->{save.run();sendRefresh();}).setNegativeButton("إغلاق",null).show();
 }
 private SeekBar.OnSeekBarChangeListener listener(Runnable r){return new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean u){r.run();}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}};}
 private void sendRefresh(){try{Intent i=new Intent(this,IslandOverlayService.class).setAction("REFRESH_STYLE");startService(i);}catch(Exception ignored){}}
}