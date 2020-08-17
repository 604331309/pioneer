package com.slk.webflux;

import com.slk.webflux.utils.ThreadUtils;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;


public class Reactor {
    public void print(Object o) {
        System.out.println("[" + Thread.currentThread().getName() + "]>>>" + o);
    }

    @Test
    public void test() throws InterruptedException {
        ThreadUtils.println(null);
        Flux
                .just("A", "B", "C")//发布数据流
                .publishOn(Schedulers.elastic())//声明一个弹性线程，使数据流的处理在子线程
                .map(old -> old += "+++")//进行转换操作
                .map(old -> old + "---")
                .subscribe(
                        ThreadUtils::println,//the consumer to invoke on each value
                        ThreadUtils::println,//the consumer to invoke on error signal
                        () -> {//the consumer to invoke on complete signal
                            ThreadUtils.println("complete");
                        });
        Thread.sleep(500L); // 可能存在主线程推出造成子线程还没执行完，所以有可能出现无法输出complete
    }

    /**
     * 背压
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        ThreadUtils.println(null);
        Flux
                .just("A", "B", "C")//发布数据流
                .publishOn(Schedulers.elastic())//声明一个弹性线程，使数据流的处理在子线程
                .map(old -> {
                    ThreadUtils.println("old1");
                    return old + "+++";
                })
                //进行转换操作
                .map(old -> old + "---")
                .subscribe( //在调用此方法后flux中的数据流才会被执行处理
                        ThreadUtils::println,//the consumer to invoke on each value （等于onNext）
                        ThreadUtils::println,//the consumer to invoke on error signal（等于onError）
                        () -> {//the consumer to invoke on complete signal （等于onComplete）
                            ThreadUtils.println("complete");
                        },
                        subscription -> { // （等于onSubscribe）背压
                            subscription.request(2);//通知上游只发布2个数据元素
                        });
    }

    private Flux<Integer> generateFluxFrom1To6() {
        return Flux.just(1, 2, 3, 4, 5, 6);
    }

    private Mono<Integer> generateMonoWithError() {
        return Mono.error(new Exception("some error"));
    }

    /**
     * 其中，expectNext用于测试下一个期望的数据元素，
     * expectErrorMessage用于校验下一个元素是否为错误信号，
     * expectComplete用于测试下一个元素是否为完成信号。
     */
    @Test
    public void testViaStepVerifier() {
        StepVerifier.create(generateFluxFrom1To6())
                .expectNext(1, 2, 3, 4, 5, 6)
                .expectComplete()
                .verify();
        StepVerifier.create(generateMonoWithError())
                .expectErrorMessage("some error")
                .verify();
    }

    /**
     * 1.Flux.range(1, 6)用于生成从“1”开始的，自增为1的“6”个整型数据；
     * 2.map接受lambdai -> i * i为参数，表示对每个数据进行平方；
     * 3.验证新的序列的数据；
     * 4.verifyComplete()相当于expectComplete().verify()。
     */
    @Test
    public void test3() {
        StepVerifier.create(Flux.range(1, 6)    // 1
                .map(i -> i * i))   // 2
                .expectNext(1, 4, 9, 16, 25, 36)    //3
                .verifyComplete();  // 4
    }

    /**
     * 1.对于每一个字符串s，将其拆分为包含一个字符的字符串流；
     * 2.对每个元素延迟100ms；
     * 3.对每个元素进行打印（注doOnNext方法是“偷窥式”的方法，不会消费数据流）；
     * 4.验证是否发出了8个元素。
     */
    @Test
    public void test4() {
        StepVerifier.create(
                Flux.just("flux", "mono")
                        .flatMap(s -> Flux.fromArray(s.split("\\s*"))   // 1
                                .delayElements(Duration.ofMillis(100))) // 2
                        .doOnNext(System.out::print)) // 3
                .expectNextCount(8) // 4
                .verifyComplete();
    }

    @Test
    public void test5() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .onErrorReturn(0)   // 1
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);

    }

    @Test
    public void test6() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .map(i -> i * i)
                .onErrorResume(original -> Flux.error(
                        new Exception("SLA exceeded", original)
                ))
                .subscribe(System.out::println, System.err::println);

    }
}
