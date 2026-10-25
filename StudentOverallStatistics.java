import java.util.ArrayList;
import java.util.List;

// کلاس آمار کلی دانشجویان
public class StudentOverallStatistics {
    private List<StudentStatistics> allStudentStats;
    private List<Student> topDelayedStudents;

    public StudentOverallStatistics(LibrarySystem system) {
        calculateStatistics(system);
    }

    private void calculateStatistics(LibrarySystem system) {
        this.allStudentStats = new ArrayList<>();
        for (Student student : system.getStudents()) {
            allStudentStats.add(new StudentStatistics(student));
        }
            
        // 4-4 مشاهده اطلاعات آماری دانشجویان همه موارد 3-6 به همراه لیست 10 دانشجوی بابیشترین تاخیر در تحویل کتاب
        this.topDelayedStudents = new ArrayList<>();
        List<Student> allStudents = new ArrayList<>(system.getStudents());
        
        // مرتب سازی بر اساس تعداد تاخیرها
        for (int i = 0; i < allStudents.size() - 1; i++) {
            for (int j = i + 1; j < allStudents.size(); j++) {
                Student s1 = allStudents.get(i);
                Student s2 = allStudents.get(j);
                
                long delays1 = countDelays(s1);
                long delays2 = countDelays(s2);
                
                if (delays1 < delays2) {
                    allStudents.set(i, s2);
                    allStudents.set(j, s1);
                }
            }
        }
        
        // انتخاب 10 دانشجوی اول
        int count = Math.min(10, allStudents.size());
        for (int i = 0; i < count; i++) {
            topDelayedStudents.add(allStudents.get(i));
        }
    }
    
    private long countDelays(Student student) {
        long count = 0;
        for (Loan loan : student.getLoanHistory()) {
            if (loan.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    // Getter methods
    public List<StudentStatistics> getAllStudentStats() { return allStudentStats; }
    public List<Student> getTopDelayedStudents() { return topDelayedStudents; }
}
