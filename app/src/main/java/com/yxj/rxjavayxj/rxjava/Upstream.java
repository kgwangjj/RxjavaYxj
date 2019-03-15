package com.yxj.rxjavayxj.rxjava;

/**
 * Author:  Yxj
 * Time:    2019/3/15 上午9:50
 * -----------------------------------------
 * Description: 上游
 */
public abstract class Upstream<T> {

    public abstract void subscribe(Downstream<T> downstream);

    public static <T> Upstream<T> createUpstream(final UpstreamSource<T> source){
        return new Upstream<T>(){
            @Override
            public void subscribe(Downstream<T> downFlow) {
                source.call(downFlow);
            }
        };
    }

    public <R> Upstream<R> map(final Function<T,R> function){

        return new Upstream<R>(){
            @Override
            public void subscribe(final Downstream<R> downFlow) {

                Upstream.this.subscribe(new Downstream<T>() {
                    @Override
                    public void onNext(T t) {
                        try {
                            R r = function.apply(t);
                            downFlow.onNext(r);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        };
    }

    /**
     * 改变上游所在的线程
     * @return
     */
    public Upstream<T> subscribeOnNewThread(){
        return new Upstream<T>() {
            @Override
            public void subscribe(final Downstream<T> downFlow) {

                new Thread(){
                    @Override
                    public void run() {
                        Upstream.this.subscribe(downFlow);
                    }
                }.start();

            }
        };
    }

    public interface UpstreamSource<T>{
        void call(Downstream<T> downstream);
    }

}