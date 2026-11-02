# سیستم مدیریت کتابخانه دانشگاه (REST API)

سرویس REST برای مدیریت کتابخانه‌ی دانشگاه، نوشته‌شده با **Java**، سرور **Jetty**، **Jersey (JAX-RS)** و پایگاه داده‌ی **SQLite**. سه نقش **دانشجو، کارمند و مدیر** و بخش **مهمان** از طریق API در دسترس‌اند. منوی کنسولی حذف شده و یک **رابط کاربری وب ساده** (HTML/CSS/JS) برای همه‌ی منوها روی همین سرویس ارائه می‌شود؛ قرار است در ادامه یک کلاینت کنسولی هم روی همین API ساخته شود.

> **توجه:** در این نسخه **هیچ احراز هویتی وجود ندارد**. هر کسی که به سرویس دسترسی داشته باشد می‌تواند همه‌ی endpointها را صدا بزند؛ آن را فقط در شبکه‌ی محلی/آزمایشی اجرا کنید. نقش کاربر (کارمند، دانشجو) با نام کاربری او در آدرس مشخص می‌شود.

## ساختار پروژه

```
se/
├── pom.xml
├── README.md
└── src/main/java/znulib/
    ├── UniversityLibrarySystem.java     نقطه‌ی شروع (راه‌اندازی سرور)
    ├── model/                           موجودیت‌ها (User, Student, Employee, Manager, Guest, Book, LoanRequest, Loan)
    ├── service/LibrarySystem.java       منطق اصلی و نگهداری وضعیت
    ├── statistics/                      گزارش‌ها و آمار
    ├── storage/DataStore.java           ذخیره‌سازی در SQLite
    ├── util/DateUtil.java
    ├── ui/                              رابط کاربری وب (پکیج جدا)
    │   ├── UiServer.java                آدرس /ui
    │   └── StaticServlet.java           سرو فایل‌های ثابت از classpath
    └── rest/                            لایه‌ی REST
        ├── ApiServer.java               راه‌اندازی Jetty + Jersey (آدرس پایه: /api)
        ├── BaseResource.java            ابزارهای مشترک (قفل هم‌زمانی، ذخیره، اعتبارسنجی، خروجی JSON)
        ├── GuestResource.java           مهمان
        ├── BookResource.java            جستجوی کتاب
        ├── StudentResource.java         دانشجو
        ├── EmployeeResource.java        کارمند
        ├── ManagerResource.java         مدیر
        └── ErrorMapper.java             خطاها به شکل JSON

src/main/resources/znulib/ui/web/        فایل‌های رابط کاربری وب
    ├── index.html                       منوی اصلی
    ├── guest.html / guest.js            منوی مهمان
    ├── student.html / student.js        منوی دانشجو
    ├── employee.html / employee.js      منوی کارمند
    ├── manager.html / manager.js        منوی مدیر
    ├── app.js                           توابع مشترک (API، فرم، جدول، منو، ورود)
    └── style.css
```

## پیش‌نیازها

- **JDK نسخه ۸ یا بالاتر** و **Apache Maven ۳.۶ یا بالاتر**
- اینترنت در اولین build (دانلود وابستگی‌ها از Maven Central)

## نحوه‌ی اجرا

```bash
mvn clean package
java -Dfile.encoding=UTF-8 -jar target/library-system.jar          # پورت پیش‌فرض 8080
java -Dfile.encoding=UTF-8 -jar target/library-system.jar 9090     # پورت دلخواه
```

یا مستقیم با Maven: `mvn compile exec:java` (برای پورت دلخواه: `-Dexec.args="9090"`).

پس از اجرا:

- رابط کاربری وب: `http://localhost:8080/ui/`
- آدرس پایه‌ی API: `http://localhost:8080/api`

پایگاه داده (`library.db`) در پوشه‌ی جاری ساخته می‌شود؛ در اولین اجرا داده‌های نمونه (مدیر، کارمند `emp1`، دانشجویان `stu1` و `stu2`، سه کتاب و یک درخواست امانت) وارد می‌شوند. برای بازگشت به داده‌های اولیه، سرور را متوقف و فایل `library.db` را حذف کنید.

## رابط کاربری وب

صفحه‌ی اصلی `http://localhost:8080/ui/` است و هر منو یک entry point جداگانه دارد؛ گزینه‌های هر صفحه همان گزینه‌های منوی کنسولی قبلی‌اند:

| صفحه | گزینه‌های منو |
|---|---|
| `/ui/index.html` | ورود دانشجو، ورود کارمند، ورود مدیر، ورود به عنوان مهمان |
| `/ui/guest.html` | مشاهده تعداد دانشجویان، جستجوی کتاب (عنوان)، مشاهده آمار کلی |
| `/ui/student.html` | جستجوی کتاب، درخواست امانت کتاب، مشاهده تاریخچه امانت‌ها، تغییر رمز عبور |
| `/ui/employee.html` | ثبت کتاب جدید، ویرایش اطلاعات کتاب، بررسی درخواست‌های امانت، ثبت دریافت کتاب بازگشتی، مشاهده گزارش دانشجو، فعال/غیرفعال کردن دانشجو، تغییر رمز عبور |
| `/ui/manager.html` | تعریف کارمند جدید، مشاهده عملکرد کارمندان، مشاهده آمار امانت‌ها، مشاهده آمار کلی دانشجویان، تغییر رمز عبور |

- چون احراز هویتی وجود ندارد، «ورود» دانشجو و کارمند فقط با **نام کاربری** انجام می‌شود (وجود آن در سیستم بررسی می‌شود) و صفحه‌ی مدیر مستقیم باز می‌شود. نام کاربری در `sessionStorage` مرورگر نگه‌داشته می‌شود و با «خروج» پاک می‌شود.
- رابط کاربری فقط از API بالا استفاده می‌کند و آدرس‌ها نسبی (`/api/...`) هستند، پس باید از همان سرور باز شود.
- فایل‌های وب داخل jar و در مسیر `znulib/ui/web/` بسته‌بندی می‌شوند؛ فقط فایل‌های `.html`، `.css` و `.js` با نام ساده سرو می‌شوند.

## قواعد کلی API

- همه‌ی ورودی و خروجی‌ها JSON هستند (`Content-Type: application/json`). تاریخ‌ها به شکل `YYYY-MM-DD`.
- **شماره‌ی کتاب** (`bookId`) جایگاه کتاب در فهرست کتاب‌ها است (از ۱) و در خروجی‌ها با نام `id` می‌آید؛ شماره‌ی درخواست و امانت همان `id` در خروجی است.
- خطاها همیشه به شکل `{"error": "پیام"}` برمی‌گردند: `400` ورودی نامعتبر، `404` پیدا نشد، `409` تعارض با وضعیت فعلی (مثلاً نام کاربری تکراری یا کتاب در امانت).
- هر تغییر بلافاصله در SQLite ذخیره می‌شود و درخواست‌ها به‌صورت هم‌زمان‌سازی‌شده پردازش می‌شوند.

## فهرست endpointها

### مهمان
| متد | آدرس | توضیح |
|---|---|---|
| GET | `/api/guest/students/count` | تعداد دانشجویان ثبت‌نام‌شده |
| GET | `/api/guest/books?title=` | جستجوی کتاب فقط بر اساس عنوان |
| GET | `/api/guest/statistics` | آمار ساده (دانشجویان، کتاب‌ها، کل امانت‌ها، امانت‌های جاری) |

### جستجوی کتاب
| متد | آدرس | توضیح |
|---|---|---|
| GET | `/api/books?title=&year=&author=` | جستجوی ترکیبی (همه‌ی پارامترها اختیاری)؛ وضعیت موجود/امانت در فیلد `available` |

### دانشجو
| متد | آدرس | بدنه | توضیح |
|---|---|---|---|
| POST | `/api/students` | `{username, password, name, studentId}` | ثبت‌نام دانشجو |
| GET | `/api/students` | | فهرست دانشجویان |
| POST | `/api/students/{username}/loan-requests` | `{bookId, startDate, endDate}` | ثبت درخواست امانت |
| GET | `/api/students/{username}/loans` | | تاریخچه‌ی امانت‌ها |
| PUT | `/api/students/{username}/password` | `{password}` | تغییر رمز عبور |

### کارمند (`{username}` = نام کاربری کارمند)
| متد | آدرس | بدنه | توضیح |
|---|---|---|---|
| POST | `/api/employees/{username}/books` | `{title, author, publicationYear, isbn}` | ثبت کتاب جدید |
| PUT | `/api/employees/{username}/books/{bookId}` | هر زیرمجموعه‌ای از فیلدهای بالا | ویرایش کتاب (فقط فیلدهای ارسالی) |
| GET | `/api/employees/{username}/loan-requests/pending` | | درخواست‌های در انتظار تایید |
| POST | `/api/employees/{username}/loan-requests/{requestId}/approve` | | تایید درخواست (فقط اگر تاریخ شروع امروز یا دیروز باشد) |
| GET | `/api/employees/{username}/loans/active` | | امانت‌های تحویل‌داده‌نشده |
| POST | `/api/employees/{username}/loans/{loanId}/return` | | ثبت دریافت کتاب بازگشتی |
| GET | `/api/employees/{username}/students/{studentUsername}/report` | | گزارش دانشجو (کل امانت‌ها، تحویل‌نشده، با تاخیر) |
| PUT | `/api/employees/{username}/students/{studentUsername}/active` | `{active: true/false}` | فعال/غیرفعال کردن دانشجو |
| PUT | `/api/employees/{username}/password` | `{password}` | تغییر رمز عبور |

### مدیر
| متد | آدرس | بدنه | توضیح |
|---|---|---|---|
| POST | `/api/manager/employees` | `{username, password, name}` | تعریف کارمند جدید |
| GET | `/api/manager/employees` | | عملکرد کارمندان |
| GET | `/api/manager/statistics/loans` | | آمار امانت‌ها (تعداد درخواست‌ها، امانت‌ها، میانگین مدت) |
| GET | `/api/manager/statistics/students` | | آمار همه‌ی دانشجویان + فهرست ۱۰ دانشجوی با بیشترین تاخیر |
| PUT | `/api/manager/password` | `{password}` | تغییر رمز عبور مدیر |

## نمونه‌ی استفاده

```bash
B=http://localhost:8080/api

# جستجوی کتاب
curl "$B/books?title=جاوا"

# دانشجو درخواست امانت می‌دهد (تاریخ شروع را امروز بگذارید)
curl -X POST -H 'Content-Type: application/json' \
     -d '{"bookId":2,"startDate":"2026-10-31","endDate":"2026-11-07"}' \
     $B/students/stu2/loan-requests

# کارمند درخواست‌های در انتظار را می‌بیند و یکی را تایید می‌کند
curl $B/employees/emp1/loan-requests/pending
curl -X POST $B/employees/emp1/loan-requests/2/approve

# ثبت بازگشت کتاب
curl -X POST $B/employees/emp1/loans/1/return

# آمار برای مدیر
curl $B/manager/statistics/loans
```

## نکات

- رمز عبورها فعلاً در پایگاه داده به‌صورت متن ساده نگهداری می‌شوند و هیچ‌جا بررسی نمی‌شوند؛ هنگام افزودن احراز هویت باید هش شوند.
- هنگام ثبت درخواست امانت، کتابِ امانت‌رفته و دانشجوی غیرفعال رد می‌شوند؛ تایید دوباره‌ی یک درخواست یا بازگشت دوباره‌ی یک امانت هم `409` می‌دهد (در منوی قدیمی با نمایش‌ندادن این موارد جلوی آن گرفته می‌شد).
