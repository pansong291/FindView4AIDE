package pansong291.findview4aide.xposedhook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class InitHook implements IXposedHookLoadPackage
{
 private static final String TAG = InitHook.class.getCanonicalName();
 public static Activity activity;
 public static ClassLoader loader;
 private static Method method_J8, method_getSelectionContent;
 private static AlertDialog dialog;
 private static CheckBox cb_private, cb_add_m, cb_root_view, cb_type_conversion;

 @Override
 public void handleLoadPackage(LoadPackageParam p1) throws Throwable
 {
  if(p1.packageName.equals("com.aide.ui1"))
  {
   loader = p1.classLoader;
   hookOnCreate();
   hookOnCreateActionMode();
   hookOnActionItemClicked();
  }
 }

 private void hookOnCreate()
 {
  try
  {
   XposedHelpers.findAndHookMethod(
    "com.aide.ui.MainActivity", loader, "onCreate", Bundle.class,
    new XC_MethodHook()
    {
     protected void afterHookedMethod(MethodHookParam param) throws Throwable
     {
      activity = (Activity) param.thisObject;
     }
    });
   log(TAG, "hook onCreate successfully");
  }catch(Throwable t)
  {
   log(TAG, "hook onCreate err:");
   XposedBridge.log(t);
  }
 }

 private void hookOnCreateActionMode()
 {
  try
  {
   XposedHelpers.findAndHookMethod(
    "com.aide.ui.Y", loader, "onCreateActionMode", ActionMode.class, Menu.class,
    new XC_MethodHook()
    {
     protected void afterHookedMethod(MethodHookParam param) throws Throwable
     {
      Menu menu = (Menu) param.args[1];
      menu.add(0, 12345, 0, "FindViewById");
     }
    });
   log(TAG, "hook onCreateActionMode successfully");
  }catch(Throwable t)
  {
   log(TAG, "hook onCreateActionMode err:");
   XposedBridge.log(t);
  }
 }

 private void hookOnActionItemClicked()
 {
  try
  {
   XposedHelpers.findAndHookMethod(
    "com.aide.ui.Y", loader, "onActionItemClicked", ActionMode.class, MenuItem.class,
    new XC_MethodHook()
    {
     protected void afterHookedMethod(MethodHookParam param) throws Throwable
     {
      MenuItem item = (MenuItem) param.args[1];
      if(item.getItemId() == 12345)
      {
       getDialog().show();
      }
     }
    });
   log(TAG, "hook onActionItemClicked successfully");
  }catch(Throwable t)
  {
   log(TAG, "hook onActionItemClicked err:");
   XposedBridge.log(t);
  }
 }

 private String getSelectionContent()
 {
  String str = null;
  try
  {
   Class clazz;
   if(method_J8 == null)
   {
    clazz = loader.loadClass("com.aide.ui.MainActivity");
    method_J8 = clazz.getDeclaredMethod("J8");
   }
   Object AIDEEditorPager = method_J8.invoke(activity);

   if(method_getSelectionContent == null)
   {
    clazz = loader.loadClass("com.aide.ui.AIDEEditorPager");
    method_getSelectionContent = clazz.getDeclaredMethod("getSelectionContent");
   }
   str = method_getSelectionContent.invoke(AIDEEditorPager).toString();
  }catch(Throwable t)
  {
   log(TAG, "get getSelectionContent err:");
   XposedBridge.log(t);
  }
  return str;
 }

 private AlertDialog getDialog()
 {
  if(dialog == null)
   dialog = new AlertDialog.Builder(activity)
    .setView(getDialogView())
    .setPositiveButton(
    "Ok", new DialogInterface.OnClickListener()
    {
     @Override
     public void onClick(DialogInterface p1, int p2)
     {
      ClipboardManager cm = (ClipboardManager)activity.getSystemService(activity.CLIPBOARD_SERVICE);
      cm.setText(getJavaCode(getSelectionContent()));
      Toast.makeText(activity, "The content has been copied to clipboard", 1).show();
     }
    })
    .create();
  return dialog;
 }

 private View getDialogView()
 {
  LinearLayout llt = new LinearLayout(activity);
  llt.setOrientation(LinearLayout.VERTICAL);
  cb_private = new CheckBox(activity);
  cb_add_m = new CheckBox(activity);
  cb_root_view = new CheckBox(activity);
  cb_type_conversion = new CheckBox(activity);
  LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
  llt.addView(cb_private, lp);
  llt.addView(cb_add_m, lp);
  llt.addView(cb_root_view, lp);
  llt.addView(cb_type_conversion, lp);
  cb_private.setText("Private");
  cb_add_m.setText("Add m");
  cb_root_view.setText("Root view");
  cb_type_conversion.setText("Type conversion");
  llt.setPadding(24, 24, 0, 0);
  return llt;
 }

 private static String getJavaCode(String xmlCode)
 {
  log(TAG, "XML CODE:\n" + xmlCode);
  if(xmlCode == null) return null;
  HashMap<String, String> map = new HashMap<>();
  StringBuilder tag = new StringBuilder();
  StringBuilder attr = new StringBuilder();
  StringBuilder value = new StringBuilder();
  char currentChar;
  boolean startRecord = false;
  for(int i = 0; i < xmlCode.length(); i++)
  {
   currentChar = xmlCode.charAt(i);
   switch(currentChar)
   {
    case '<':
     tag.delete(0, tag.length());
     for(; ++i < xmlCode.length();)
     {
      currentChar = xmlCode.charAt(i);
      if(Character.isWhitespace(currentChar))
      {
       if(startRecord)
       {
        startRecord = false;
        break;
       }
      }else
      {
       startRecord = true;
       tag.append(currentChar);
      }
     }
     int pointIndex = tag.lastIndexOf(".");
     if(pointIndex >= 0)
     {
      tag.delete(0, pointIndex + 1);
     }
     log(TAG, "tag:<" + tag.toString());
     break;

    case '"':
     value.delete(0, value.length());
     for(; ++i < xmlCode.length();)
     {
      currentChar = xmlCode.charAt(i);
      if(currentChar == '"')
      {
       if(startRecord)
       {
        startRecord = false;
        break;
       }
      }else
      {
       startRecord = true;
       value.append(currentChar);
       if(currentChar == '\\')
       {
        value.append(xmlCode.charAt(++i));
       }
      }
     }
     if(attr.toString().equals("android:id"))
     {
      map.put(value.toString(), tag.toString());
     }
     log(TAG, "=\"" + value.toString() + "\"");
     break;

    default:
     if(Character.isLetter(currentChar))
     {
      attr.delete(0, attr.length());
      for(; i < xmlCode.length(); i++)
      {
       currentChar = xmlCode.charAt(i);
       if(currentChar == '=')
       {
        if(startRecord)
        {
         startRecord = false;
         break;
        }
       }else if(!Character.isWhitespace(currentChar))
       {
        startRecord = true;
        attr.append(currentChar);
       }
      }
      log(TAG, "\t" + attr.toString());
     }
   }
  }
  Set keySet = map.keySet();
  StringBuilder sb1 = new StringBuilder();
  StringBuilder sb2 = new StringBuilder();
  sb2.append("private void initView(");
  if(cb_root_view.isChecked()) sb2.append("View view");
  sb2.append(")\n{\n");
  boolean isAndroidId;
  String clazz;
  String varName;
  for(String id: keySet)
  {
   // class
   clazz = map.get(id);
   log(TAG, "class: " + clazz + ", id: " + id);

   // id
   isAndroidId = id.startsWith("@android:id/");
   int divisionSignIndex = id.indexOf("/");
   if(divisionSignIndex >= 0)
    id = id.substring(divisionSignIndex + 1);

   // variable name need add a prefix m ?
   if(cb_add_m.isChecked())
   {
    StringBuilder vN = new StringBuilder();
    String[] words = id.split("_");
    for(int i = 0; i < words.length; i++)
    {
     vN.append(Character.toUpperCase(words[i].charAt(0)));
     if(words[i].length() > 1)
      vN.append(words[i].substring(1));
    }
    varName = "m" + vN.toString();
   }else
    varName = id;

   // is private ?
   if(cb_private.isChecked())sb1.append("private ");
   sb1.append(clazz + " " + varName + ";\n");

   // need add type conversion ?
   sb2.append("\t" + varName + " = ");
   if(cb_type_conversion.isChecked())
    sb2.append("(" + clazz + ") ");

   // need add root view ?
   if(cb_root_view.isChecked())sb2.append("view.");
   sb2.append("findViewById(");
   if(isAndroidId) sb2.append("android.");
   sb2.append("R.id." + id + ");\n");
  }
  sb1.append("\n");
  sb2.append("}");
  varName = sb1.toString() + sb2.toString();
  log(TAG, varName);
  return varName;
 }

 public static void log(String tag, String s)
 {
  XposedBridge.log(tag + ", " + s);
 }

}
