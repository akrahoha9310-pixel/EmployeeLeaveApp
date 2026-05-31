# نظام الحركة والمهام — سياق المشروع

> **للمحادثة الجديدة:** اطلب من الوكيل: «اقرأ `PROJECT.md` في `C:\garage_system` ثم [المطلوب]».

**آخر تحديث للملف:** 2026-05-29 (مراجعة شاملة — D/E/P + جلسة + persist)  
**الاختبارات:** `flutter test` — **57** اختباراً

---

## 1. الهدف

تطبيق Flutter عربي (RTL) لإدارة:

1. **طلبات مهمة سيارة** — موافقات + تعيين مركبة/سائق من المرآب + تنفيذ الموظف.
2. **أذونات الخروج** — نفس منطق الموافقات **بدون مرآب**.

حالياً **عرض تجريبي (demo)**:

- **البيانات التشغيلية** (مهام، أذونات، شات، سجل نشاط، مفاتيح إشعارات مقروءة) تُحفظ محلياً عبر **`DemoDataStore`** (JSON في `shared_preferences`، مفتاح `demo_data_v1`).
- **إعدادات المستخدم** (لغة، سمة، تنبيهات، قوائم مدمجة) عبر **`AppPreferences`** (نفس `shared_preferences`).
- **الجلسة** (`AppSession` / `AppUser`) — تُحفظ محلياً عبر **`SessionStore`** حتى **تسجيل الخروج**؛ آخر اختيارات الدخول اقتراح في **`LoginPreferences`**.
- **الفرع إداري/فني** يُستنتج من **نوع المديرية** (`DemoOrg`) — لا اختيار منفصل في نماذج الطلب.

---

## 2. قرارات ثابتة (لا تغيّرها إلا بطلب صريح)

| القرار | التفاصيل |
|--------|----------|
| اللغة والاتجاه | عربي افتراضي + English؛ RTL/LTR حسب `AppPreferences.language`؛ `flutter_localizations` + `localizationsDelegates` في `MaterialApp` |
| مفاتيح الأدوار | إنجليزي في الكود (`admin`, `employee`, …) لتفادي مشاكل المقارنة على Chrome |
| التخزين | Stores in-memory + **`DemoDataStore`** (طلبات/شات/نشاط) + **`AppPreferences`** (إعدادات) + **`SessionStore`** (جلسة) + **`LoginPreferences`** (اقتراحات دخول) — كلها `shared_preferences` |
| مفاتيح المديريات | عربي في `demo_org.dart` (مفاتيح البيانات) — العرض في EN عبر **`DemoOrgL10n`** |
| شكل البيانات | كلاسات typed في `lib/models/` — المهام وأذونات الخروج |
| لا Hive | مسموح `shared_preferences` للإعدادات **وبيانات العرض التجريبي** — لا Hive |
| السيرفر | **آخر مرحلة** — المستخدم يريد إكمال الواجهة والمنطق أولاً |
| الأدمن | يرى كل أزرار الإجراءات (للاختبار) عبر `_actsAs` في workflow |
| لوحات منفصلة | مهام: `DashboardPage` — أذونات: `ExitDashboardPage` |
| تسجيل الخروج | يمسح **الجلسة المحفوظة** (`SessionStore`) و**مقروءية الإشعارات** — **لا** يمسح الطلبات و**لا** آخر اختيارات الدخول |
| الجلسة | **`SessionStore`** — تبقى بعد إغلاق التطبيق حتى logout صريح؛ **`LoginPreferences`** — اقتراح دور/مديرية/اسم (قابل للتغيير) |
| الفرع (إداري/فني) | يُحدَّد من المديرية عبر `DemoOrg.missionTypeForDirectorate` / `exitSectionForDirectorate` — ليس اختياراً يدوياً في النماذج |
| تقسيم `main.dart` | تم (~53 سطر). **لا تعيد تشغيل** `tool/split_main.dart` إلا مع نسخة احتياطية كاملة من main القديم |

### الفرع حسب المديرية (`demo_org.dart`)

| نوع المديرية | أمثلة | مسار المهمة | مسار إذن الخروج | قسم فرعي |
|--------------|-------|-------------|-----------------|----------|
| **إدارية** (٨) | الشؤون المالية، التخطيط… | مدير ← HR ← مرآب ← موظف | مدير ← HR ← موظف | لا |
| **فنية** (٣ منشورات) | صحيفة الثورة، المجلة… | مدير ← مرآب ← موظف | مدير ← موظف | نعم (مراسلين/مصورين/محررين) |

- **`OrgScope`** و **workflow** يعتمدان المديرية كمصدر حقيقة للفرع.
- **`DirectorateBranchHint`** في نماذج الطلب يعرض الفرع المستنتج.

---

## 3. الأدوار والصلاحيات (`lib/app_roles.dart`)

| المفتاح | التسمية العربية |
|---------|------------------|
| `admin` | أدمن |
| `employee` | موظف |
| `manager` | مدير |
| `hr` | موارد بشرية |
| `garage` | مرآب |

| الصلاحية | من يملكها |
|----------|-----------|
| طلب مهمة / إذن خروج | `employee`, `admin` |
| سجل المركبات / سجل السائقين | `garage`, `admin` |
| لوحة المرآب والمهام | `manager`, `hr`, `garage`, `admin` |
| عمليات المرآب (طابور التعيين) | `garage`, `admin` |
| غرفة التنسيق (شات المديرية) | `manager`, `hr`, `garage`, `admin` — غرفة لكل مديرية |
| الغرفة العامة (كل المدراء + HR + مرآب + أدمن) | `manager`, `hr`, `garage`, `admin` — غرفة واحدة |
| محادثة على الطلب | حسب الدور — الموظف على طلباته؛ المدير/HR/المرآب حسب الصلاحية |
| لوحة أذونات الخروج | `employee`, `manager`, `hr`, `admin` (ليس `garage`) |

**تسجيل الدخول (demo):** أي اسم مستخدم/كلمة مرور — يُختار الدور من Radio. عند الدخول الناجح: **`SessionStore.persist()`** + **`LoginPreferences.save()`**. عند فتح التطبيق: **`SessionStore.restore()`** — إن وُجدت جلسة → **`MainShell`** مباشرة؛ وإلا **`LoginScreen`** مع اقتراح آخر اختيارات. الجلسة في **`AppSession`** كـ **`AppUser`** (`userId`, `displayName`, `currentRole`, `directorate`, `department`). للموظف والمدير: مديرية + قسم فرعي. للموارد البشرية: مديرية (للعرض). أسماء المديريات/الأقسام في القوائم والشريط العلوي تُعرَض عبر **`DemoOrgL10n`** في وضع English. **كلمة المرور لا تُحفظ.**

**فلترة المتابعة:** المدير يرى طلبات مديريته فقط؛ HR يرى كل الطلبات الإدارية في مرحلته (بدون فلترة مديرية).

---

## 4. دورة حياة مهمة السيارة

### المسار حسب المديرية (يُستنتج تلقائياً)

- **مديرية إدارية** → مسار إداري (مع HR).
- **مديرية فنية** → مسار فني (بدون HR) + قسم فرعي إلزامي للمنشورات.

النموذج: اختيار **المديرية** فقط — لا dropdown «نوع المهمة». المنطق: `mission_workflow.dart` — التخزين: `mission_store.dart` — الوصول: `Repositories.mission` — الفلترة: `mission_visibility.dart` + `org_scope.dart`.

### مراحل رئيسية (`MissionStages`)

`awaitingDepartmentManager` → `awaitingHr` (إداري فقط) → `awaitingGarage` → `readyToExecute` → `inProgress` → `completed` / `rejected` / `cancelled`.

### بيانات تجريبية

- مركبات وسائقون افتراضيون عبر `Repositories.mission.cars` / `.drivers`.
- المرآب: تعيين، رفض، صيانة أسطول، بطاقات في `DashboardPage`.

---

## 5. دورة حياة إذن الخروج

### الحقول (نموذج الطلب)

الاسم الكامل، **المديرية** (يُستنتج منها الفرع `إداري`/`فني` ويُخزَّن في `section`)، السبب، التاريخ، وقت الخروج من–إلى. للمنشورات الفنية: قسم فرعي من جلسة المستخدم عند تسجيل الدخول.

### المسار حسب المديرية

- **إدارية:** مدير ← HR ← جاهز للخروج ← موظف.
- **فنية:** مدير ← جاهز للخروج ← موظف.

**بدون مرآب.** المنطق: `exit_permission_workflow.dart` — التخزين: `exit_permission_store.dart` — الوصول: `Repositories.exitPermission` — الواجهات: `exit_permission_pages.dart` (طلب + `ExitPendingRequestsPage` غلاف رفيع).

---

## 6. هيكل الملفات

```
lib/
  models/
    user.dart                  # AppUser + UserSnapshot
    mission_enums.dart         # MissionStage, MissionStatus, MissionType, …
    mission_request.dart
    car.dart
    driver.dart
    exit_enums.dart            # ExitStage, ExitSection
    exit_permission.dart
    chat_message.dart
    activity_event.dart        # ActivityEvent, ActivityAction
  theme/
    app_theme.dart             # AppColors، buildAppTheme() + buildAppDarkTheme()
  persistence/
    demo_data_codec.dart       # JSON encode/decode
    demo_data_seed.dart         # عيّنة تجريبية بمراحل متنوعة
    demo_data_store.dart       # load/save/exportJson/importJson — مفتاح demo_data_v1
    session_store.dart         # جلسة دخول مستمرة حتى logout
    login_preferences.dart     # آخر اختيارات شاشة الدخول (اقتراح)
  main.dart                    # load prefs + demo data + restore session → GarageApp
  app_roles.dart
  app_session.dart             # AppUser? current، login/loginAs/logout/clearMemory
  app_navigation.dart          # openAppPage — فتح صفحة فوق الهيكل
  app_refresh.dart             # bump() → notifier + DemoDataStore.save()
  app_preferences.dart         # لغة، سمة، تنبيهات، قوائم مدمجة + shared_preferences
  follow_up_counts.dart        # عدّ badges تبويب المتابعة
  request_visibility_base.dart # فلترة مشتركة للمهام والأذونات
  notification_navigation.dart # فتح الطلب من الإشعار/النشاط
  request_schedule_validation.dart  # تحقق تاريخ/وقت موحّد
  request_schedule_messages.dart    # رسائل خطأ النماذج
  workflow_navigation_hint.dart     # SnackBar + تنقل بعد اعتماد workflow
  l10n/
    app_strings.dart           # كل نصوص الواجهة ar/en (runtime — لا تستخدم داخل const)
    enum_l10n.dart             # ترجمة تسميات الـ enums (.localizedLabel)
    demo_org_l10n.dart         # أسماء المديريات/الأقسام بالإنجليزية للعرض
    compact_layout.dart        # مسافات القوائم المدمجة
  activity_log.dart            # سجل نشاطات — ي persist مع DemoDataStore
  activity_visibility.dart     # فلترة السجل حسب الدور
  notification_inbox.dart      # inbox + مقروء/غير مقروء — مفاتيح المقروء ت persist
  demo_org.dart                # مديريات + missionType/exitSection من المديرية
  org_scope.dart               # مطابقة الطلبات مع نطاق الجلسة
  chat_access.dart
  chat_store.dart
  repositories/
    repositories.dart          # Repositories.mission / .exitPermission / .chat
    mission_repository.dart
    exit_permission_repository.dart
    chat_repository.dart
  mission_store.dart
  mission_workflow.dart
  mission_visibility.dart
  exit_permission_store.dart
  exit_permission_workflow.dart
  exit_permission_visibility.dart
  screens/
    exit_permission_pages.dart   # طلب إذن + ExitPendingRequestsPage (غلاف رفيع)
    login_screen.dart            # DemoOrgL10n في قوائم المديرية/القسم
    main_shell.dart              # الهيكل + شريط علوي (DemoOrgL10n) + تبويبات
    app_tab_pages.dart           # FollowUpTab، GarageTab، ChatTab، MoreTab
    home_page.dart               # HomeGridTab — ملخص + خريطة تنقل حسب الدور
    settings_page.dart           # لغة، سمة، reset/export/import demo، ملف شخصي
    activity_log_page.dart       # سجل النشاطات + فلتر + pull-to-refresh
    notifications_page.dart      # مركز الإشعارات + pull-to-refresh
    mission_request_page.dart    # طلب مهمة — مديرية أولاً، فرع مستنتج
    pending_requests_page.dart   # غلاف → PendingListPage<MissionRequest>
    active_operations_page.dart  # غلاف → PendingListPage + MissionActiveCard
    archive_page.dart            # غلاف → PendingListPage + MissionArchiveCard
    exit_active_permissions_page.dart  # غلاف → PendingListPage + ExitActiveCard
    exit_archive_page.dart       # غلاف → PendingListPage + ExitArchiveCard
    garage_page.dart
    garage_queue_page.dart
    fleet_management_page.dart
    garage_drivers_page.dart
    dashboard_page.dart
    exit_dashboard_page.dart
    chat_pages.dart
    coordination_chat_page.dart
    global_coordination_chat_page.dart
  widgets/
    app_bottom_nav.dart
    app_page_scaffold.dart
    pattern_background.dart
    service_grid_card.dart
    request_ui.dart              # RequestCard + DirectorateBranchHint
    request_filters.dart
    request_list_utils.dart
    pending_list_page.dart       # قائمة عامة: متابعة + جارية + أرشيف
    mission_pending_card.dart
    mission_active_card.dart
    mission_archive_card.dart
    exit_pending_card.dart
    exit_active_card.dart
    exit_archive_card.dart
    home_overview_strip.dart
    directorate_dropdown_field.dart  # مديريات مجمّعة إدارية/فنية
    demo_data_backup_dialogs.dart    # تصدير/استيراد JSON من الإعدادات
    highlight_request_list.dart
    notification_bell.dart
    dashboard_stat_card.dart
tool/
  split_main.dart                # سكربت تقسيم قديم — لا تشغّل عشوائياً
test/                            # 57 اختبار
  widget_test.dart
  app_session_test.dart
  mission_workflow_test.dart
  mission_visibility_test.dart
  exit_permission_workflow_test.dart
  exit_permission_visibility_test.dart
  follow_up_counts_test.dart
  notification_inbox_test.dart
  chat_test.dart
  demo_data_store_test.dart
  demo_data_seed_test.dart
  session_persistence_test.dart
  request_schedule_validation_test.dart
  workflow_navigation_hint_test.dart
  demo_org_test.dart
  org_scope_test.dart
```

### الهيكل والتنقل (`main_shell.dart`)

بعد تسجيل الدخول يفتح **`MainShell`** (alias: `EmployeeHomePage`) — **تبويبات سفلية**:

| التبويب | المحتوى | ملاحظات |
|---------|---------|---------|
| الرئيسية | `HomeGridTab` | طلبات سريعة + روابط حسب الدور |
| المتابعة | `FollowUpTab` | badge أحمر على التبويب + ملخص العدد |
| المرآب | `GarageTab` | يظهر لـ manager / hr / garage / admin |
| المحادثة | `ChatTab` | يظهر لمن يملك صلاحية الشات |
| المزيد | `MoreTab` | إعدادات، أرشيف، لوحات |

- **شريط علوي:** اسم المستخدم، الدور/المديرية (`DemoOrgL10n`)، 🔔 إشعارات، إعدادات، تسجيل خروج.
- **الصفحات الفرعية** تُفتح عبر `openAppPage` فوق الهيكل (`AppPageScaffold`).
- **تحديث العدادات والحفظ:** `AppRefresh.bump()` بعد أي تغيير على الطلبات → يحدّث badges **ويستدعي** `DemoDataStore.save()`.

### تدفق البيانات (مهم)

```text
main()
  → AppPreferences.load()
  → DemoDataStore.load()        # طلبات، شات، نشاط → Stores in-memory
  → SessionStore.restore()      # AppSession.loginAs إن وُجدت جلسة
  → GarageApp.home = MainShell | LoginScreen

الشاشات → Repositories → Store (typed) → Workflow / Visibility
         ↘ AppRefresh.bump() → notifier (UI) + DemoDataStore.save() → shared_preferences

login ناجح → SessionStore.persist() + LoginPreferences.save()
logout     → AppSession.clearMemory() + SessionStore.clear()
```

### مفاتيح shared_preferences

| المفتاح / الملف | المحتوى |
|-----------------|---------|
| `demo_data_v1` | بيانات تشغيلية (مهام، أذونات، شات، نشاط، إشعارات مقروءة) |
| `demo_session_v1` | جلسة `AppUser` محفوظة |
| `last_login_*` | آخر دور، مديرية، قسم، اسم مستخدم (اقتراح) |
| `pref_*` | لغة، سمة، تنبيهات، قوائم مدمجة |

### عناصر تبويب الرئيسية (`home_page.dart` → `HomeGridTab`)

- طلب مهمة سيارة / طلب إذن خروج (موظف+أدمن)
- متابعة طلبات المهام / أذونات الخروج
- سجل المركبات + سجل السائقين (مرآب+أدمن)
- لوحات، أرشيف، عمليات المرآب، شات — حسب الدور

---

## 7. ما اكتمل (نسخة مستقرة — demo)

### نواة التطبيق
- [x] تسجيل دخول متعدد الأدوار + **`AppUser`** في `AppSession` + **جلسة مستمرة** (`SessionStore`)
- [x] دورة مهمة كاملة (موافقات، مرآب، تنفيذ، أرشيف)
- [x] دورة إذن خروج منفصلة (بدون مرآب)
- [x] لوحات منفصلة للمهام وأذونات الخروج
- [x] سياق تنظيمي (`directorate` / `department`) + فلترة visibility
- [x] طبقة **`Repositories`** — الشاشات لا تستدعي Store مباشرة
- [x] **`MainShell`** + تبويبات سفلية + badges المتابعة

### واجهة وتجربة
- [x] تصميم موحّد: `PatternBackground`, `ServiceCircleTile`, `AppPageScaffold`, `RequestCard`
- [x] **i18n:** `AppStrings` + `EnumL10n` + **`DemoOrgL10n`** (أسماء مديريات EN)
- [x] لغة ar/en، RTL/LTR، سمة نهار/ليل/نظام، قوائم مدمجة
- [x] **`SettingsPage`** — ملف شخصي، إعدادات، حول التطبيق v1.0.0
- [x] **بحث + فلتر مرحلة** على: المتابعة، الأرشيف، الجارية، طابور المرآب، أذونات الخروج
- [x] **pull-to-refresh** على كل القوائم أعلاه + اللوحات + الإشعارات + سجل النشاط
- [x] **المرحلة A:** `AppRefresh.bump()` موحّد، dark mode، إصلاحات LTR
- [x] **المرحلة B:** هيكل `models/`، نقل `exit_permission_pages`، اختبارات visibility/جلسة/إشعارات
- [x] **المرحلة C:** **`DemoDataStore`** persistence، `request_list_utils`، اختبار round-trip
- [x] **مراجعة P0/P1:** `isPendingFollowUp`، workflow guards، أرشيف rejected/cancelled، `DemoOrgL10n` موسّع، dark mode
- [x] **مراجعة P2:** `DemoDataStore.resetAll()`، `NotificationNavigation`، `RequestVisibilityBase`، `HighlightRequestList`، **`PendingListPage<T>`** + بطاقات منفصلة
- [x] **P0/D0/D1 (الرئيسية):** `HomeOverviewStrip` + `FollowUpCounts` موسّع + خريطة تنقل + تلميح الدور
- [x] **P-Nav:** `WorkflowNavigationHint` — SnackBar مع زر تنقل بعد اعتماد/تخصيص/تنفيذ
- [x] **D2/D3:** `DirectorateDropdownField` + `DemoDataSeed` + زر تعبئة في الإعدادات
- [x] **D4:** `DemoDataStore.exportJson` / `importJson` + حوار تصدير/استيراد في الإعدادات
- [x] **E1:** صفحات الجارية/الأرشيف → `PendingListPage` + بطاقات منفصلة
- [x] **P-Date:** `RequestScheduleValidation` — تاريخ/وقت موحّد في نماذج المهمة وإذن الخروج
- [x] **E3:** `SessionStore` + `LoginPreferences` — جلسة مستمرة + اقتراح آخر اختيارات دخول

### سجل وإشعارات
- [x] **`ActivityLog`** — تسجيل تلقائي + صفحة سجل + persist
- [x] **`NotificationInbox`** + **`NotificationNavigation`** — فتح الطلب من الإشعار مع تمييز

### اختبارات (`flutter test` — 57)
- [x] workflow مهام + أذونات + visibility + follow-up + إشعارات + جلسة + شات
- [x] **`demo_data_store_test`** — codec + save/load + resetAll + export/import
- [x] **`demo_data_seed_test`** — عيّنة تجريبية
- [x] **`session_persistence_test`** — SessionStore + LoginPreferences
- [x] **`request_schedule_validation_test`** — تاريخ/وقت موحّد
- [x] **`workflow_navigation_hint_test`** — تنقل بعد اعتماد
- [x] **`demo_org_test`** — استنتاج الفرع من المديرية
- [x] **`org_scope_test`** — نطاق المديرية + الفرع
- [x] **`widget_test`** — شاشة الدخول

### بنية تقنية
- [x] تقسيم `main.dart` إلى `screens/` / `widgets/` / `theme/` / `persistence/`
- [x] `flutter_localizations` + إزالة `hive_flutter`
- [x] تشغيل Chrome + تنظيف deprecated warnings في `lib/`

---

## 8. ما لم يُنفَّذ بعد

### 8.1 بنية تحتية (بعد طلب صريح — لا تبدأ بدون موافقة)

| # | الموضوع | الملخص | يعتمد على |
|---|---------|--------|-----------|
| 1 | **Hive أو سيرفر** | Hive = تخزين محلي أسرع على جهاز واحد؛ سيرفر = قاعدة مركزية + API للإنتاج | — |
| 2 | **مزامنة / نسخ سحابي** | نفس البيانات على عدة أجهزة؛ يحتاج سيرفر أو Firebase/Supabase | (1) |
| 3 | **JWT / SSO** | دخول حقيقي؛ الدور والمديرية من الخادم لا من Radio | (1) + غالباً (2) |

### 8.2 تحسينات واجهة مقترحة (بدون سيرفر)

**مرحلة D — أولوية عالية (demo أقوى)**

| # | المهمة | الحالة |
|---|--------|--------|
| D0 | **تلميح الدور + خريطة تنقل** على الرئيسية | ✅ |
| D1 | **ملخص أرقام** على الرئيسية | ✅ |
| D2 | **تجميع المديريات** في dropdown (إدارية \| فنية) | ✅ |
| D3 | **زر «تعبئة بيانات تجريبية»** | ✅ |
| D4 | **تصدير / استيراد JSON** | ✅ |

**مرحلة P — من مراجعة UX**

| # | المهمة | الحالة |
|---|--------|--------|
| P-Nav | **Navigation بعد اعتماد** — snackbar + اقتراح «اذهب للمرآب» | ✅ |
| P-Date | **قيد تاريخ/وقت** موحّد (مهمة + إذن خروج) | ✅ |

**مرحلة E — أولوية متوسطة (تجربة + صيانة)**

| # | المهمة | الملاحظة |
|---|--------|----------|
| E1 | **دمج صفحات الجارية والأرشيف** في `PendingListPage` + بطاقات | ✅ |
| E2 | **آخر ٥ نشاطات** على الرئيسية | `activity_log.dart`, `home_page.dart` |
| E3 | **تذكّر آخر دور/مديرية** + **بقاء مسجّل الدخول** | ✅ |
| E4 | **Onboarding** — شريحة واحدة أول فتح | صفحة أو dialog |

**مرحلة F — polish (اختياري)**

| # | المهمة |
|---|--------|
| F1 | Swipe إجراءات سريعة على البطاقات |
| F2 | فلتر تاريخ في الأرشيف |
| F3 | PDF / طباعة طلب |
| F4 | Empty states غنية حسب الدور |

---

## 9. أين نكمل؟ (توصية حالية)

```
الوضع الآن: demo مستقر — قوائم موحّدة + persist محلي + جلسة + 57 اختبار
                    ↓
        ┌───────────┴───────────┐
        │                       │
   مسار العرض              مسار الإنتاج
   (بدون سيرفر)            (بعد قراركم)
        │                       │
   D + P + E1/E3 ✅         Hive أو API
   التالي: E2 أو E4         ثم مزامنة
                            ثم JWT/SSO
```

**التوصية:** **E2** (آخر ٥ نشاطات على الرئيسية) — أو **E4** (Onboarding).

**لا تُنفَّذ Hive/سيرفر/JWT** إلا بطلب صريح (§2 + §10).

---

## 10. تشغيل واستكشاف الأخطاء

### اعتماديات رئيسية (`pubspec.yaml`)

- `flutter_localizations` (SDK) — Material/Cupertino localizations
- `shared_preferences` — إعدادات + بيانات demo + جلسة + اقتراحات دخول
- `cupertino_icons`

```text
cd C:\garage_system
C:\flutter\bin\flutter.bat pub get
C:\flutter\bin\flutter.bat run -d chrome
C:\flutter\bin\flutter.bat test
C:\flutter\bin\flutter.bat analyze
```

| المشكلة | الحل |
|---------|------|
| `No MaterialLocalizations found` | `flutter_localizations` + `localizationsDelegates` في `MaterialApp` |
| `AppStrings` في `const` | أزل `const` — النصوص runtime |
| `Building with plugins requires symlink support` | تفعيل **وضع المطوّر** في Windows |
| فقدان **الطلبات** بعد refresh | تحقق من `DemoDataStore.load()` في `main()` و`AppRefresh.bump()` بعد التعديلات |
| بيانات **تالفة** بعد تحديث | `DemoDataStore` يتجاهل JSON تالف ويبقي الافتراضي — امسح مفتاح `demo_data_v1` من prefs أو أضف reset |
| فقدان **الإعدادات** | تحقق من `AppPreferences.load()` في `main()` |
| فتح التطبيق دائماً على **الدخول** رغم عدم logout | تحقق من `SessionStore.restore()` في `main()` ومفتاح `demo_session_v1` |
| **اقتراحات الدخول** لا تظهر | تحقق من `LoginPreferences.load()` في `login_screen.dart` |
| اختبارات unit وفشل `SharedPreferences` | `DemoDataStore.save()` / `SessionStore` يتخطيان الحفظ إذا لا binding — استخدم `TestWidgetsFlutterBinding.ensureInitialized()` |
| CanvasKit hot restart error | full restart أو `--web-renderer html` |

---

## 11. تعليمات للوكيل في محادثة جديدة

1. اقرأ هذا الملف قبل أي تعديل واسع.
2. النصوص عبر `AppStrings` / `EnumL10n` / `DemoOrgL10n` — **لا** `const` مع `AppStrings`.
3. **الشاشات** → `Repositories` فقط — لا `MissionStore` / `ExitPermissionStore` من UI.
4. بعد تغيير الطلبات: **`AppRefresh.bump()`** (يحفظ تلقائياً).
5. **`main()`** يجب أن يستدعي بالترتيب: `AppPreferences.load()` → **`DemoDataStore.load()`** → **`SessionStore.restore()`**.
6. مفاتيح المديريات في البيانات **عربية** — العرض EN عبر `DemoOrgL10n.directorate()` / `.department()`.
7. لا Hive ولا سيرفر إلا بطلب صريح.
8. لا تعيد دمج الشاشات في `main.dart`.
9. بعد مرحلة كبيرة: حدّث §7 و§8 و§9 و§6 وتاريخ الملف (§13).

---

## 12. جملة جاهزة للصق في محادثة جديدة

```
المشروع: C:\garage_system
تطبيق Flutter عربي/إنجليزي — نظام الحركة والمهام (مهام سيارة + أذونات خروج).

اقرأ C:\garage_system\PROJECT.md كاملاً قبل أي تعديل — المرجع الوحيد لحالة المشروع.

ملخص سريع:
- بيانات demo: DemoDataStore (JSON، مفتاح demo_data_v1) — load في main()، save عند AppRefresh.bump().
- إعدادات: AppPreferences (لغة، سمة، تنبيهات، قوائم مدمجة).
- جلسة: SessionStore (demo_session_v1) — تبقى حتى logout؛ LoginPreferences — اقتراح آخر اختيارات دخول.
- Repositories للواجهة؛ Stores داخلياً؛ workflow/visibility منفصلان.
- MainShell + تبويبات (رئيسية، متابعة+badge، مرآب، محادثة، المزيد).
- i18n: AppStrings + EnumL10n + DemoOrgL10n (مديريات EN).
- PendingListPage<T> موحّد: متابعة + جارية + أرشيف (مهام وأذونات).
- فرع إداري/فني من المديرية (DemoOrg) — لا اختيار يدوي في النماذج.
- RequestScheduleValidation — تاريخ/وقت موحّد في النماذج.
- 57 اختبار: flutter test
- تشغيل: C:\flutter\bin\flutter.bat run -d chrome

التزم بـ PROJECT.md (لا store من UI، لا Hive/سيرفر إلا بطلب).

بعد القراءة: أكد بجملة واحدة، ثم اسألني ماذا نعمل.
```

---

## 13. قائمة تحقق بعد كل مرحلة كبيرة

1. حدّث **تاريخ** الملف و§7 (ما اكتمل) و§8 (ما تبقى) و§9 (توصية المتابعة).
2. عدّل §2 إذا تغيّر قرار ثابت (API، تخزين، هيكل بيانات).
3. عدّل §6 إذا أُضيفت ملفات أو مجلدات مهمة.
4. شغّل `flutter test` و`flutter analyze` — سجّل العدد في رأس الملف.
5. (اختياري) commit أو tag — النسخة المستقرة = `lib/` + `test/` كامل.
6. افتح **محادثة جديدة** ب§11 إذا المرحلة الجاية موضوع مختلف.
