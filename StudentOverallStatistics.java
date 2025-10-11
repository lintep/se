import java.util.List;
import java.util.stream.Collectors;

// کلاس آمار کلی دانشجویان
public class StudentOverallStatistics {
    private List<StudentStatistics> allStudentStats;
    private List<Student> topDelayedStudents;

    public StudentOverallStatistics(LibrarySystem system) {
        calculateStatistics(system);
    }

    private void calculateStatistics(LibrarySystem system) {
        this.allStudentStats = system.getStudents().stream()
            .map(StudentStatistics::new)
            .collect(Collectors.toList());
            
        // 4-4 مشاهده اطلاعات آماری دانشجویان همه موارد 3-6 به همراه لیست 10 دانشجوی بابیشترین تاخیر در تحویل کتاب
        this.topDelayedStudents = system.getStudents().stream()
            .sorted((s1, s2) -> {
                long delays1 = s1.getLoanHistory().stream().filter(Loan::isOverdue).count();
                long delays2 = s2.getLoanHistory().stream().filter(Loan::isOverdue).count();
                return Long.compare(delays2, delays1);
            })
            .limit(10)
            .collect(Collectors.toList());
    }

    // Getter methods
    public List<StudentStatistics> getAllStudentStats() { return allStudentStats; }
    public List<Student> getTopDelayedStudents() { return topDelayedStudents; }
}