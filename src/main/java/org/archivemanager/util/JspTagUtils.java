package org.archivemanager.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;


/**
 * Created by jnewman on 4/26/18.
 */
public class JspTagUtils {

  public static boolean hasProperty(Object o, String propertyName){

    if (o == null || propertyName == null) {
      return false;
    }
    BeanInfo beanInfo;
    try {
      beanInfo = java.beans.Introspector.getBeanInfo(o.getClass());

    } catch (IntrospectionException e) {
      return false;
    }

    for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
      if (propertyName.equals(pd.getName())) {
        return true;
      }
    }

    return false;
  }

}
