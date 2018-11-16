package jack.com.jkutilsdemo.model_0;

import jack.com.jkutilsdemo.model.Student;

public class Teacher extends Student {

    public Teacher(String name, int age, int grade) {
        super(name, age, grade);
    }

    public String subject;

    public Student favoriteStudent;
}
