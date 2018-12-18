package com.reactiveandroid.internal.notifications;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Insert;
import com.reactiveandroid.query.Update;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestModel;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ModelChangeNotifierTest extends BaseTest {

    @Test
    public void testModelNotifications() {
        OnModelChangedListener<TestModel> onModelChangedListener = mock(OnModelChangedListener.class);
        ModelChangeNotifier.get().registerForModelChanges(TestModel.class, onModelChangedListener);

        for (int i = 0; i < 5; i++) {
            TestModel model = new TestModel();
            model.stringField = "Text" + i;
            model.save();
            verify(onModelChangedListener).onModelChanged(model, ChangeAction.INSERT);
            model.stringField = "Text" + i * i;
            model.save();
            verify(onModelChangedListener).onModelChanged(model, ChangeAction.UPDATE);
            model.delete();
            verify(onModelChangedListener).onModelChanged(model, ChangeAction.DELETE);
        }
    }

    @Test
    public void testTableNotifications() {
        OnTableChangedListener onTableChangedListener = mock(OnTableChangedListener.class);
        ModelChangeNotifier.get().registerForTableChanges(TestModel.class, onTableChangedListener);

        Insert.into(TestModel.class).columns("stringField", "intField").values("Title", "10").execute();
        verify(onTableChangedListener).onTableChanged(TestModel.class, ChangeAction.INSERT);
        Update.table(TestModel.class).set("stringField = ?, intField = ?", "Title 2", "100").where("id=1").execute();
        verify(onTableChangedListener).onTableChanged(TestModel.class, ChangeAction.UPDATE);
        Delete.from(TestModel.class).execute();
        verify(onTableChangedListener).onTableChanged(TestModel.class, ChangeAction.DELETE);
    }


}
