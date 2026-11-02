// توابع مشترک رابط کاربری: فراخوانی API، فرم، جدول، منو و ورود
const UI = (() => {
  const $ = s => document.querySelector(s);
  const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  const pad = n => String(n).padStart(2, '0');
  const today = (plus = 0) => {
    const d = new Date(); d.setDate(d.getDate() + plus);
    return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
  };

  function msg(text, ok = true) {
    const m = $('#msg');
    m.textContent = text || '';
    m.className = text ? (ok ? 'ok' : 'err') : '';
  }

  async function guard(fn) {
    try { await fn(); } catch (e) { msg(e.message, false); }
  }

  async function api(method, path, body) {
    const res = await fetch('/api' + path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : {},
      body: body ? JSON.stringify(body) : undefined
    });
    const data = res.status === 204 ? null : await res.json().catch(() => null);
    if (!res.ok) throw new Error((data && data.error) || ('خطا ' + res.status));
    return data;
  }

  const box = parent => parent.appendChild(document.createElement('div'));

  // fields: [{name, label, type, value, required, options:[{value,label}]}]
  function form(parent, fields, submitLabel, onSubmit) {
    const f = document.createElement('form');
    f.innerHTML = fields.map(x => '<label>' + esc(x.label) + (x.required ? ' *' : '') +
      (x.options
        ? '<select name="' + x.name + '">' + x.options.map(o => '<option value="' + esc(o.value) + '"' +
            (String(o.value) === String(x.value) ? ' selected' : '') + '>' + esc(o.label) + '</option>').join('') + '</select>'
        : '<input name="' + x.name + '" type="' + (x.type || 'text') + '" value="' + esc(x.value) + '"' + (x.required ? ' required' : '') + '>') +
      '</label>').join('') + '<button type="submit">' + esc(submitLabel) + '</button>';
    f.onsubmit = e => {
      e.preventDefault();
      msg('');
      const values = {};
      fields.forEach(x => { values[x.name] = f.elements[x.name].value.trim(); });
      guard(() => onSubmit(values));
    };
    parent.appendChild(f);
    return f;
  }

  // cols: [{title, key | value(row)}]  actions: [{label | label(row), run(row)}]
  function table(parent, rows, cols, actions = []) {
    if (!rows.length) { parent.insertAdjacentHTML('beforeend', '<p class="empty">موردی یافت نشد.</p>'); return; }
    const t = document.createElement('table');
    t.innerHTML = '<thead><tr>' + cols.map(c => '<th>' + esc(c.title) + '</th>').join('') + (actions.length ? '<th></th>' : '') + '</tr></thead>';
    const body = document.createElement('tbody');
    rows.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = cols.map(c => '<td>' + esc(c.value ? c.value(r) : r[c.key]) + '</td>').join('');
      if (actions.length) {
        const td = document.createElement('td');
        actions.forEach(a => {
          const b = document.createElement('button');
          b.textContent = typeof a.label === 'function' ? a.label(r) : a.label;
          b.onclick = () => guard(() => a.run(r));
          td.appendChild(b);
        });
        tr.appendChild(td);
      }
      body.appendChild(tr);
    });
    t.appendChild(body);
    const wrap = document.createElement('div');
    wrap.className = 'table-wrap';
    wrap.appendChild(t);
    parent.appendChild(wrap);
  }

  // pairs: [[label, value], ...]
  function kv(parent, pairs) {
    const dl = document.createElement('dl');
    dl.innerHTML = pairs.map(p => '<dt>' + esc(p[0]) + '</dt><dd>' + esc(p[1]) + '</dd>').join('');
    parent.appendChild(dl);
  }

  function passwordForm(parent, path) {
    form(parent, [{ name: 'password', label: 'رمز عبور جدید', type: 'password', required: true }], 'تغییر رمز عبور', async v => {
      await api('PUT', path, { password: v.password });
      msg('رمز عبور با موفقیت تغییر یافت.');
    });
  }

  // items: [{title, run(panel)} | {title, href}]
  let current = null;
  function render(item, keepMsg) {
    if (!keepMsg) msg('');
    const p = $('#panel');
    p.innerHTML = '<h2>' + esc(item.title) + '</h2>';
    return guard(() => item.run(p));
  }
  const reload = () => render(current, true);

  function menu(items) {
    const nav = $('#menu');
    nav.innerHTML = '';
    items.forEach(item => {
      const b = document.createElement('button');
      b.textContent = item.title;
      b.onclick = () => {
        if (item.href) { location.href = item.href; return; }
        nav.querySelectorAll('button').forEach(x => x.classList.remove('active'));
        b.classList.add('active');
        current = item;
        render(item, false);
      };
      nav.appendChild(b);
    });
  }

  // ورود با نام کاربری (فعلاً بدون رمز عبور): lookup(username) نام نمایشی را برمی‌گرداند یا خطا می‌دهد
  function session(role, lookup, onReady) {
    async function enter(username) {
      $('#who').textContent = await lookup(username);
      sessionStorage.setItem(role, username);
      $('#login').hidden = true;
      $('#app').hidden = false;
      $('#exit').hidden = false;
      msg('');
      onReady(username);
    }
    form($('#login'), [{ name: 'username', label: 'نام کاربری', required: true }], 'ورود', v => enter(v.username));
    $('#exit').onclick = () => { sessionStorage.removeItem(role); location.href = 'index.html'; };
    const saved = sessionStorage.getItem(role);
    if (saved) enter(saved).catch(() => sessionStorage.removeItem(role));
  }

  return { $, esc, today, msg, api, box, form, table, kv, passwordForm, menu, reload, session };
})();
