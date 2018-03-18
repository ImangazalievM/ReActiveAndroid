package com.reactiveandroid.internal.serializer.integrationtest;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(database = MyDatabase .class)
public class ModelWithCustomField extends Model {

    enum MyCustomType {
        ONE("ONE"), TWO("TWO");

        private final String name;

        MyCustomType(String name) {
            this.name = name;
        }

        public boolean equals(String name) {
            return this.name.equals(name);
        }

        public String toString() {
            return this.name;
        }

        public static MyCustomType getByName(String name) {
            for (MyCustomType myCustomType : values()) {
                if (myCustomType.equals(name)) return myCustomType;
            }
            return null;
        }

    }

    @PrimaryKey
    public Long id;
    @Column
    public MyCustomType myCustomField;

    public ModelWithCustomField() {
    }

    public ModelWithCustomField(MyCustomType myCustomField) {
        this.myCustomField = myCustomField;
    }

}
