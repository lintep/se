// منوی مهمان
const bookCols = [
  { title: 'شماره', key: 'id' }, { title: 'عنوان', key: 'title' }, { title: 'نویسنده', key: 'author' },
  { title: 'سال نشر', key: 'publicationYear' }, { title: 'شابک', key: 'isbn' },
  { title: 'وضعیت', value: b => b.available ? 'موجود' : 'امانت داده شده' }
];

UI.menu([
  { title: 'مشاهده تعداد دانشجویان', run: async p => {
      const r = await UI.api('GET', '/guest/students/count');
      UI.kv(p, [['تعداد دانشجویان ثبت‌نام شده', r.count]]);
  } },
  { title: 'جستجوی کتاب', run: p => {
      UI.form(p, [{ name: 'title', label: 'عنوان کتاب' }], 'جستجو', async v => {
        out.innerHTML = '';
        UI.table(out, await UI.api('GET', '/guest/books?title=' + encodeURIComponent(v.title)), bookCols);
      });
      const out = UI.box(p);
  } },
  { title: 'مشاهده آمار کلی', run: async p => {
      const s = await UI.api('GET', '/guest/statistics');
      UI.kv(p, [['تعداد دانشجویان', s.totalStudents], ['تعداد کتاب‌ها', s.totalBooks],
                ['تعداد کل امانت‌ها', s.totalLoans], ['تعداد امانت‌های جاری', s.currentLoans]]);
  } },
  { title: 'بازگشت به منوی اصلی', href: 'index.html' }
]);
