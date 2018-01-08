# DlibTest

此專案是利用 [Dlib](https://github.com/tzutalin/dlib-android-app) 偵測人臉，並加上一些裝飾的練習。

### Problem
若是用gradle建置Dlib環境是會有問題的，可能是它gradle沒弄好，會遇到[這個問題](https://github.com/tzutalin/dlib-android-app/issues/14)，問題是shape_predictor.dat 這檔案不會更新到最新版，所以偵測的到臉在哪，但無法拿到臉部特徵。
目前想到的解法是不用gradle建置環境，用JNI架好環境，若只是寫好玩的可以像我一樣，安裝它的demo apk，它會自動幫你更新shape_predictor.dat。
