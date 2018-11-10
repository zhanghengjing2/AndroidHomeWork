package es.source.code.observer;


import android.util.Log;

import es.source.code.model.DishList;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public abstract class SimpleObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onEvent(t);

    }

    @Override
    public void onError(Throwable e) {
        Log.d("SimpleObserver",e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.d("SimpleObserver","onComplete");
    }

    public abstract void onEvent(T t);


}
