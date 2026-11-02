// منوی کارمند
const bookCols = [
  { title: 'شماره', key: 'id' }, { title: 'عنوان', key: 'title' }, { title: 'نویسنده', key: 'author' },
  { title: 'سال نشر', key: 'publicationYear' }, { title: 'شابک', key: 'isbn' },
  { title: 'وضعیت', value: b => b.available ? 'موجود' : 'امانت داده شده' }
];
const studentCols = [
  { title: 'نام کاربری', key: 'username' }, { title: 'نام', key: 'name' }, { title: 'شماره دانشجویی', key: 'studentId' },
  { title: 'وضعیت', value: s => s.active ? 'فعال' : 'غیرفعال' }
];
const bookFields = (b = {}) => [
  { name: 'title', label: 'عنوان کتاب', value: b.title, required: true },
  { name: 'author', label: 'نام نویسنده', value: b.author, required: true },
  { name: 'publicationYear', label: 'سال نشر', type: 'number', value: b.publicationYear, required: true },
  { name: 'isbn', label: 'شابک (ISBN)', value: b.isbn, required: true }
];

UI.session('employee', async username => {
  // نام کارمند از فهرست عملکرد کارمندان گرفته می‌شود
  const e = (await UI.api('GET', '/manager/employees')).find(x => x.username === username);
  if (!e) throw new Error('کارمند پیدا نشد: ' + username);
  return e.name;
}, username => {
  const me = '/employees/' + encodeURIComponent(username);
  UI.menu([
    { title: 'ثبت کتاب جدید', run: p => UI.form(p, bookFields(), 'ثبت کتاب', async v => {
        const b = await UI.api('POST', me + '/books', { ...v, publicationYear: Number(v.publicationYear) });
        UI.msg('کتاب با موفقیت ثبت شد (شماره ' + b.id + ').');
        UI.reload();
    }) },
    { title: 'ویرایش اطلاعات کتاب', run: async p => {
        UI.table(p, await UI.api('GET', '/books'), bookCols, [{ label: 'ویرایش', run: b => {
          p.innerHTML = '<h2>ویرایش کتاب شماره ' + b.id + '</h2>';
          UI.form(p, bookFields(b), 'ذخیره تغییرات', async v => {
            await UI.api('PUT', me + '/books/' + b.id, { ...v, publicationYear: Number(v.publicationYear) });
            UI.msg('اطلاعات کتاب با موفقیت به‌روزرسانی شد.');
            UI.reload();
          });
        } }]);
    } },
    { title: 'بررسی درخواست‌های امانت', run: async p => {
        const cols = [{ title: 'شماره', key: 'id' }, { title: 'دانشجو', key: 'student' }, { title: 'کتاب', key: 'bookTitle' },
                      { title: 'تاریخ شروع', key: 'startDate' }, { title: 'تاریخ پایان', key: 'endDate' }];
        UI.table(p, await UI.api('GET', me + '/loan-requests/pending'), cols, [{ label: 'تایید', run: async r => {
          const loan = await UI.api('POST', me + '/loan-requests/' + r.id + '/approve');
          UI.msg('درخواست امانت تایید شد. شماره امانت: ' + loan.id);
          UI.reload();
        } }]);
    } },
    { title: 'ثبت دریافت کتاب بازگشتی', run: async p => {
        const cols = [{ title: 'شماره امانت', key: 'id' }, { title: 'دانشجو', key: 'student' }, { title: 'کتاب', key: 'bookTitle' },
                      { title: 'تاریخ شروع', key: 'startDate' }, { title: 'تاریخ پایان', key: 'endDate' }];
        UI.table(p, await UI.api('GET', me + '/loans/active'), cols, [{ label: 'ثبت بازگشت', run: async l => {
          await UI.api('POST', me + '/loans/' + l.id + '/return');
          UI.msg('بازگشت کتاب با موفقیت ثبت شد.');
          UI.reload();
        } }]);
    } },
    { title: 'مشاهده گزارش دانشجو', run: async p => {
        const students = await UI.api('GET', '/students');
        UI.table(p, students, studentCols, [{ label: 'گزارش', run: async s => {
          const r = await UI.api('GET', me + '/students/' + encodeURIComponent(s.username) + '/report');
          out.innerHTML = '<h2>گزارش دانشجو: ' + UI.esc(r.name) + '</h2>';
          UI.kv(out, [['تعداد کل امانت‌ها', r.totalLoans], ['تعداد کتاب‌های تحویل داده نشده', r.notReturnedCount],
                      ['تعداد امانت‌های با تاخیر', r.delayedReturnsCount]]);
        } }]);
        const out = UI.box(p);
    } },
    { title: 'فعال/غیرفعال کردن دانشجو', run: async p => {
        UI.table(p, await UI.api('GET', '/students'), studentCols, [{ label: s => s.active ? 'غیرفعال کردن' : 'فعال کردن', run: async s => {
          await UI.api('PUT', me + '/students/' + encodeURIComponent(s.username) + '/active', { active: !s.active });
          UI.msg(s.active ? 'دانشجو غیرفعال شد.' : 'دانشجو فعال شد.');
          UI.reload();
        } }]);
    } },
    { title: 'تغییر رمز عبور', run: p => UI.passwordForm(p, me + '/password') }
  ]);
});
