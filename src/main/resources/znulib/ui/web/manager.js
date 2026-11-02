// منوی مدیر
const studentStatCols = [
  { title: 'نام کاربری', key: 'username' }, { title: 'نام', key: 'name' }, { title: 'تعداد کل امانت‌ها', key: 'totalLoans' },
  { title: 'تحویل داده نشده', key: 'notReturnedCount' }, { title: 'امانت‌های با تاخیر', key: 'delayedReturnsCount' }
];

UI.menu([
  { title: 'تعریف کارمند جدید', run: p => UI.form(p, [
      { name: 'username', label: 'نام کاربری', required: true },
      { name: 'password', label: 'رمز عبور', type: 'password', required: true },
      { name: 'name', label: 'نام کامل', required: true }
    ], 'ایجاد کارمند', async v => {
      await UI.api('POST', '/manager/employees', v);
      UI.msg('کارمند جدید با موفقیت ایجاد شد.');
      UI.reload();
  }) },
  { title: 'مشاهده عملکرد کارمندان', run: async p => UI.table(p, await UI.api('GET', '/manager/employees'), [
      { title: 'نام کاربری', key: 'username' }, { title: 'نام', key: 'name' },
      { title: 'کتاب‌های ثبت شده', key: 'booksRegistered' }, { title: 'کتاب‌های امانت داده شده', key: 'booksLoanedOut' },
      { title: 'کتاب‌های تحویل گرفته شده', key: 'booksReceived' }
  ]) },
  { title: 'مشاهده آمار امانت‌ها', run: async p => {
      const s = await UI.api('GET', '/manager/statistics/loans');
      UI.kv(p, [['تعداد درخواست‌های امانت ثبت شده', s.totalLoanRequests], ['تعداد کل امانت‌های داده شده', s.totalLoansApproved],
                ['میانگین مدت امانت (روز)', s.averageLoanDuration.toFixed(2)]]);
  } },
  { title: 'مشاهده آمار کلی دانشجویان', run: async p => {
      const s = await UI.api('GET', '/manager/statistics/students');
      p.insertAdjacentHTML('beforeend', '<h3>همه‌ی دانشجویان</h3>');
      UI.table(p, s.students, studentStatCols);
      p.insertAdjacentHTML('beforeend', '<h3>دانشجویان با بیشترین تاخیر در تحویل کتاب</h3>');
      UI.table(p, s.topDelayedStudents.filter(x => x.delayedReturnsCount > 0), studentStatCols);
  } },
  { title: 'تغییر رمز عبور', run: p => UI.passwordForm(p, '/manager/password') },
  { title: 'بازگشت به منوی اصلی', href: 'index.html' }
]);
