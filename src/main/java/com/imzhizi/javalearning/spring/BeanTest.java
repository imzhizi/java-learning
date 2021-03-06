package com.imzhizi.javalearning.spring;

import lombok.Data;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * created by zhizi
 * on 3/13/20 21:41
 */
public class BeanTest {
    /**
     * 总的来看，在 {@link GenericApplicationContext }之下
     * 包含了 {@link GenericXmlApplicationContext } 和 {@link org.springframework.context.annotation.AnnotationConfigApplicationContext } 两个子类
     * 一个用于传统的XML格式的容器初始化，一个则应对注解式的容器初始化
     * 其中 GenericXmlApplicationContext 使用 XmlBeanDefinitionReader 完成 XML 解析
     * 而 AnnotationConfigApplicationContext 则依靠 AnnotatedBeanDefinitionReader 完成 class.config 的解析
     *
     */


    /**
     * 可以看到
     * 无论是BeanFactory、还是 ApplicationContext
     * 都是通过某种方式将bean加载到 context 对象中
     * 然后在bean不同的生命周期阶段，都能执行一些自定义方法
     */
    @Test
    public void BeanFactoryTest() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions("BeanTest.xml");

        System.out.println("student");
        System.out.println(factory.getBean("student"));
        System.out.println(StuDetail.count);
        System.out.println(factory.getBean("student"));
        System.out.println(StuDetail.count);
        System.out.println("teacher");
        System.out.println(factory.getBean("teacher"));
        System.out.println(Teacher.count);
        System.out.println(factory.getBean("teacher"));
        System.out.println(Teacher.count);

        System.out.println(factory.getBeanDefinition("teacher"));
    }

    @Test
    public void FXAppContextTest() {
        GenericApplicationContext context = new GenericXmlApplicationContext("BeanTest.xml");
        WorkAbility work = (WorkAbility) context.getBean("teacher");
        System.out.println(work.getWorkPlace());
    }

    @Test
    public void Bean的初始化和销毁() {
        GenericApplicationContext context = new GenericXmlApplicationContext("BeanTest.xml");
        InitDisBean initDisBean = (InitDisBean) context.getBean("initDisBean");
        System.out.println(initDisBean);
        WorkAbility work = (WorkAbility) context.getBean("teacher");
        System.out.println(work);
        context.registerShutdownHook();
    }

    @Data
    static class StuDetail extends Student {
        private Integer age;
        private Date date;
        static int count = 0;

        public StuDetail() {
            super();
            count++;
        }
    }

    @Data
    static class Teacher implements WorkAbility {
        private String username;
        private Integer gender;
        public static int count = 0;

        public Teacher() {
            count++;
        }

        @Override
        public String getWorkPlace() {
            return "school";
        }

        @PostConstruct
        public void work() {
            System.out.println("我来上班了……");
        }

        @PreDestroy
        public void done() {
            System.out.println("下班了……");
        }
    }

    @Data
    static class Student {
        private String username;
        private String pwd;
        private String gender;
    }

    interface WorkAbility {
        String getWorkPlace();
    }

    @Data
    static class InitDisBean implements InitializingBean, DisposableBean {
        private String name;

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("接口 start");
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("接口 end");
        }

        public void init() {
            System.out.println("配置 init");
        }

        public void des() {
            System.out.println("配置 des");
        }
    }

    static class Processor implements BeanPostProcessor {

        public void init() {
            System.out.println("我来了");
        }

        public void explode() {
            System.out.println("我裂开了");
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("postProcessBeforeInitialization " + beanName);
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("postProcessAfterInitialization " + beanName);
            return bean;
        }
    }
}
