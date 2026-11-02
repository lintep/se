// منوی دانشجو
const bookCols = [
  { title: 'شماره', key: 'id' }, { title: 'عنوان', key: 'title' }, { title: 'نویسنده', key: 'author' },
  { title: 'سال نشر', key: 'publicationYear' }, { title: 'شابک', key: 'isbn' },
  { title: 'وضعیت', value: b => b.available ? 'موجود' : 'امانت داده شده' }
];
const loanCols = [
  { title: 'شماره امانت', key: 'id' }, { title: 'کتاب', key: 'bookTitle' }, { title: 'تاریخ شروع', key: 'startDate' },
  { title: 'تاریخ پایان', key: 'endDate' }, { title: 'تاریخ بازگشت', value: l => l.returnDate || '—' },
  { title: 'وضعیت', value: l => l.returned ? 'تحویل داده شده' : 'در دست امانت' },
  { title: 'تاخیر (روز)', key: 'daysOverdue' }
];

UI.session('student', async username => {
  const s = (await UI.api('GET', '/students')).find(x => x.username === username);
  if (!s) throw new Error('دانشجو پیدا نشد: ' + username);
  return s.name;
}, username => {
  const me = '/students/' + encodeURIComponent(username);
  UI.menu([
    { title: 'جستجوی کتاب', run: p => {
        UI.form(p, [{ name: 'title', label: 'عنوان کتاب' }, { name: 'year', label: 'سال نشر', type: 'number' },
                    { name: 'author', label: 'نام نویسنده' }], 'جستجو', async v => {
          out.innerHTML = '';
          UI.table(out, await UI.api('GET', '/books?' + new URLSearchParams(v)), bookCols);
        });
        const out = UI.box(p);
    } },
    { title: 'درخواست امانت کتاب', run: async p => {
        const books = (await UI.api('GET', '/books')).filter(b => b.available);
        if (!books.length) { p.insertAdjacentHTML('beforeend', '<p class="empty">هیچ کتابی برای امانت موجود نیست.</p>'); return; }
        UI.form(p, [
          { name: 'bookId', label: 'کتاب', options: books.map(b => ({ value: b.id, label: b.title + ' — ' + b.author })) },
          { name: 'startDate', label: 'تاریخ شروع امانت', type: 'date', value: UI.today(), required: true },
          { name: 'endDate', label: 'تاریخ پایان امانت', type: 'date', value: UI.today(14), required: true }
        ], 'ثبت درخواست', async v => {
          await UI.api('POST', me + '/loan-requests', { bookId: Number(v.bookId), startDate: v.startDate, endDate: v.endDate });
          UI.msg('درخواست امانت با موفقیت ثبت شد!');
          UI.reload();
        });
    } },
    { title: 'مشاهده تاریخچه امانت‌ها', run: async p => UI.table(p, await UI.api('GET', me + '/loans'), loanCols) },
    { title: 'تغییر رمز عبور', run: p => UI.passwordForm(p, me + '/password') }
  ]);
});
