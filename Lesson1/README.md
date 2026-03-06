# Отчет по практической работе №1
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23  
**ФИО:** Хречко Роман Викторович  
**Номер по списку:** 25

---

## 1. Цель работы

Изучение основ создания мобильных приложений с помощью Android Studio. Создать несколько модулей для изучения отдельных областей в изучаемой среде. Изучить виды группировки элементов в дизайне, настройку интерфейса под различную ориентацию экрана телефона, а также обработка событий при нажатии на кнопки.

## 2. Структура проекта
В практической работе будут созданы 3 модуля для выполнения каждого задания:
1. `layouttype` — Изучение разметки с помощью контейнеров;
2. `control_lesson1` — Создание разметок для различной ориентации устройства с использованием ConstraintLayout;
3. `ButtonClicker` — Обработка событий при нажатии на кнопки.

---

## 3. Выполнение работы

### Задание 1. Модуль `layouttype`
Были созданы 2 разметки для изучения двух контейнеров (LinearLayout и TableLayout).

#### 1.1 LinearLayout
В первом случае был создан линейный вертикальный контейнер, который хранит два других линейных контейнера. Два контейнера горизонтальные и хранят по 3 кнопки. Всем кнопкам были выставлены равные веса `layout_weight="1"`.

**Листинг `res/layout/linear_layout.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:orientation="horizontal">

        <Button
            android:id="@+id/button19"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <Button
            android:id="@+id/button20"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <Button
            android:id="@+id/button21"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:orientation="horizontal">

        <Button
            android:id="@+id/button1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <Button
            android:id="@+id/button14"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <Button
            android:id="@+id/button8"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

    </LinearLayout>
</LinearLayout>
```

#### 1.2 TableLayout
Во втором случае используется контейнер таблицы (TableLayout), в котором находятся 3 контейнера рядов (TableRow). Всем элементам внутри рядов также были выставлены одинаковые веса. Для каждого ряда использовались различные элементы:
1. 2 кнопки и текст;
2. Кнопка и checker box;
3. Кнопка с изображением и 2 обычные кнопки.

**Листинг res/layout/table_layout.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1" >

        <Button
            android:id="@+id/button11"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="TextView" />

        <Button
            android:id="@+id/button13"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />
    </TableRow>

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1" >

        <Button
            android:id="@+id/button15"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <CheckBox
            android:id="@+id/checkBox"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="CheckBox" />
    </TableRow>

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1">

        <ImageButton
            android:id="@+id/imageButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            app:srcCompat="@android:drawable/ic_lock_power_off" />

        <Button
            android:id="@+id/button16"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />

        <Button
            android:id="@+id/button17"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Button" />
    </TableRow>
</TableLayout>
```

### Задание 2. Модуль control_lesson1

**Цель:** Создать пользовательский интерфейс с использованием ConstraintLayout и реализовать смену ориентации экрана.

#### 2.1 Создание интерфейса карточки контакта

Первой задачей является создание интерфейса карточки контакта с помощью ConstraintLayout. Были использованы ImageView, TextView, EditText и Button. Часть элементов была заключена в таблицу, которая соединена с ImageView с помощью ConstraintLayout.

**Листинг res/layout/activity_main.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:adjustViewBounds="true"
        android:cropToPadding="true"
        android:scaleType="fitCenter"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:srcCompat="@drawable/ic_launcher_background" />

    <TableLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginEnd="8dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/imageView">

        <TableRow
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1">

            <TextView
                android:id="@+id/textView3"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Name:"
                android:textAlignment="viewStart"
                android:textSize="24sp" />

            <EditText
                android:id="@+id/editTextText"
                style="@style/Widget.AppCompat.EditText"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:layout_marginTop="0dp"
                android:layout_marginEnd="16dp"
                android:layout_weight="10"
                android:ems="10"
                android:inputType="text"
                android:text="Name Surname"
                android:textAlignment="viewStart" />
        </TableRow>

        <TableRow
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1">

            <TextView
                android:id="@+id/textView2"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Organisation:"
                android:textSize="24sp" />

            <EditText
                android:id="@+id/editTextText2"
                style="@style/Widget.AppCompat.EditText"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:layout_marginTop="0dp"
                android:layout_marginEnd="16dp"
                android:layout_weight="10"
                android:ems="10"
                android:inputType="text"
                android:text="Org" />

        </TableRow>

        <TableRow
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1">

            <ImageView
                android:id="@+id/imageView2"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_weight="1"
                app:srcCompat="@android:drawable/stat_sys_phone_call_forward" />

            <EditText
                android:id="@+id/editTextText3"
                style="@style/Widget.AppCompat.EditText"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:layout_marginTop="0dp"
                android:layout_marginEnd="16dp"
                android:layout_weight="10"
                android:ems="10"
                android:inputType="phone"
                android:text="+7(555)555-55-55"
                android:textAlignment="viewStart" />
        </TableRow>

    </TableLayout>

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="8dp"
        android:layout_marginBottom="8dp"
        android:backgroundTint="#4CAF50"
        android:insetTop="0dp"
        android:insetBottom="0dp"
        android:text="SAVE"
        android:textAlignment="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### 2.2 Смена ориентации экрана

Для изучения изменения интерфейса под смену ориентации экрана был создан интерфейс с 6 кнопками для портретной разметки activity_second.xml, а также дополнительный файл разметки с пометкой land для горизонтального положения экрана. Также в классе MainActivity был изменён выбор layot, чтобы запускался новый файл, а не карточка контакта.

**Листинг MainActivity.java:**

```Java
package ru.mirea.khrechkorv.control_lesson1;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
    }
}
```

### Задание 3. Модуль ButtonClicker
Требуется создать интерфейс из двух кнопок, которые изменяют текст и значение checkerbox. Для этого используется два способа. Для первой кнопки создаётся метод внутри метода OnCreate и передаётся нужной копки через setOnClickListener. Вторая кнопка настраивается через файл разметки, для кнопки описывается название метода в поле атрибута onClick.

**Листинг MainActivity.java:**

```Java
package ru.mirea.khrechkorv.buttonclicker;

import android.os.Bundle;import android.view.View;import android.widget.Button;import android.widget.CheckBox;import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView textViewStudent;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        textViewStudent = findViewById(R.id.tvOut);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        checkBox = findViewById(R.id.checkBox);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                textViewStudent.setText("Мой номер по списку №25");
                checkBox.setChecked(true);
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);
    }

    public void onMyButtonClick(View view){
        textViewStudent.setText("Это не я сделал");
        checkBox.setChecked(false);
    }

}
```

### 4. Вывод
В итоге работы была изучена среда Android Studio, создан проект с 3 модулями для выполнения каждой из задач. Были изучены инструменты группировки элементов с помощью LinearLayout и TableLayout. С помощью ConstraintLayout был создан интерфейс карточки контакта, а также интерфейс из 6 кнопок, который был адаптирован под различные ориентации экрана. Также был создан модуль для изучения событий нажатия на кнопки с помощью двух способов.