package learningSpark.dataReadingAndSaving;

import scala.Serializable;

public class Student implements Serializable { // 需要可序列化
    Integer id;
    String name;
    Double score;

    public Student(String name, Double score, Integer id) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public Student() { // 需要有默认构造器，否则解析的时候会把报错：JsonMappingException: No suitable constructor found
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String toString() {
        return "Student [id=" + id + " name=" + name + " score=" + score + "]";
    }
}
