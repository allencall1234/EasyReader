/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easyreader.base;


public abstract class BasePresenter<T extends IView> {
    private T view;

    public BasePresenter(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onDestroy() {
        //防止强引用activity的实例造成内存泄漏
        view = null;
    }

    protected boolean isViewNotNull() {
        return view != null;
    }


    public interface LifeStyle {
        String onCreate = "onCreate";
        String onStart = "onStart";
        String onResume = "onResume";
        String onStop = "onStop";
        String onPause = "onPause";
        String onDestroy = "onDestroy";
    }

    public static void notifyIPresenter(String methodName, BasePresenter[] mAllPresenters) {
        for (BasePresenter presenter : mAllPresenters) {
            if (presenter != null) {
                switch (methodName) {
                    case LifeStyle.onStart:
                        presenter.onStart();
                        break;
                    case LifeStyle.onResume:
                        presenter.onResume();
                        break;
                    case LifeStyle.onStop:
                        presenter.onStop();
                        break;
                    case LifeStyle.onPause:
                        presenter.onPause();
                        break;
                    case LifeStyle.onDestroy:
                        presenter.onDestroy();
                        break;
                }
            }
        }
    }


}
