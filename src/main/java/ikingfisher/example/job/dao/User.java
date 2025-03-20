package ikingfisher.example.job.dao;


public class User {
    private String name;
    private Integer age;

    public User() {}

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {return name;}
    public Integer getAge() {return age;}

    public void setName(String name) {this.name = name;}
    public void setAge(Integer age) {this.age = age;}
}
