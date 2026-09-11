package ProjectPSW.support.Utility;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    public static <T> T getBean(Class<T> classeBean) {
        return context.getBean(classeBean);
    }









}
