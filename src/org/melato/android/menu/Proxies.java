package org.melato.android.menu;

import java.lang.reflect.Method;

import android.util.Log;

public class Proxies {
  
  public static void print(String msg) {
    System.out.println(msg);
    Log.i("aa", msg);
  }
  
  public static String toString(Method method, Object[] args) {
    StringBuilder buf = new StringBuilder();
    buf.append(method.getReturnType().getName());
    buf.append(" ");
    buf.append(method.getName());
    buf.append("(");
    for(int i = 0; i < args.length; i++ ) {
      if ( i > 0) {
        buf.append( ", ");        
      }
      buf.append(args[i]);
    }
    buf.append(")");
    return buf.toString();
  }
  
  public static void print(Method method, Object[] args) {
    print(toString(method, args));
  }
}
