# Multithreading
### 该md不详尽.具体参照summarize.txt
    正式开始学习多线程的项目。
    昨晚无意中发现了 百度阅读，里面的书虽然都是要钱的，但是关于java的书还真挺多。
    上午上班的路上看了一路的关于多线程的书。
----
    synchronized关键字是不能继承的（也就是子类中的父类的方法，默认并没有synchronized）
    Thread.currentThread().getName()显示当前线程的名字
    sleep()不会释放锁
    想要给类的static对象加锁，可以写synchronized(className.class){}

    volatile 用来修饰变量，表示这个变量会被多个线程访问，当线程读取这个变量的值的时候，每次都会去主存中读取（有一个寄存器，大概相当于缓存）
    可以通过volatile保证可见性，也就是，任意数据一旦发生变化，其他线程能够马上看见.
    当一个变量的修改方法没有同步，且该变量没有被volatile修饰，那么当A线程修改该变量的时候，B线程并不能读取到最新值。
    在可行的情况下不使用synchronized而使用volatile，可以提高性能。

    notify()，notifyAll(),wait()这些方法之所以放在Object类中而不是Thread类中，是因为，线程的锁是对象级的，
    这些通知或者等待方法，都是等待或通知 获得或等待同一个对象锁的线程。
        使用notify()方法唤醒其他线程后，并不代表其他线程能马上获取到锁。
        多线程并非一定比单线程快，因为假设单核CPU，处理若干个线程，其实是每个线程执行一段时间片，因为每一段时间片的时间很短，所以看起来是
    并发执行的。但是这种方式还需要记录每个线程的上下文，需要一定的开销。所谓上下文，就是当A线程切换到B，再切回A的时候，Cpu需要记录A之间的状态。
    所以在数据量少的情况下，单线程有时比多线程还要快。
        减少上下文切换的方法
            1.无锁并发编程，将数据Id按照hash算法取模分段，每个线程处理不同的段的数据、
            2.CAS算法，atomic包使用cas算法来更新数据，不需要加锁。
            3.使用最少线程。
            4.协程，在单线程中实现多任务的调度，并在单线程中维持多个任务间的切换。
        线程之间的通讯：共享内存和消息传递

    ！！！！当进入static的synchronized方法中时，获取的是类级别的锁，而不是对象级别的锁，这个时候这个类的所有对象都被锁定了。

    获取对象的锁之后，不能修改这个对象的引用，否则会让其他线程可以进入这个对象的其他同步块。

    使用run()方法，不是异步的，使用start()才是异步。

    线程安全：当多个线程访问同一个类或对象的时候，如果这个类（对象）的值一直都是预期中的，那么它就是线程安全的。

    线程的两种实现方式
    1.继承Thread
    public class ThreadTest extends Thread{
        @Override
        public void run() {
            while(true){
                System.out.println("继承Thread的多线程类");
            }
        }
        public static void main(String args[]){
            new ThreadTest().run();
        }
    }
    2.实现Runnable接口
    public class RunnableTest implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.println("实现Runnable接口的多线程类");
            }
        }
        public static void main(String args[]) {
            new Thread(new RunnableTest()).start();
        }
    }
---
    问题探讨 ：
        public class RunnableTest implements Runnable {
            private int i = 100;
            public synchronized void decrement(){
                i--;
            }
            public synchronized int getI(){
                return i;
            }
            @Override
            public void run() {
                while (i >0) {
                    decrement();
                    System.out.println("当前值：" + getI());
                }
            }
            public static void main(String args[]) {
                new Thread(new RunnableTest(),"thread1").start();
                new Thread(new RunnableTest(),"thread2").start();
                new Thread(new RunnableTest(),"thread2").start();
            }
        }
    这样子的写法，结果i并没有同步。我觉得可能是因为每开启一个线程的时候，都new了一个新的对象，所以操作的不是同一个对象中的i,
    但是这样子，为什么即使输出是不同步，但是也没有每个值都输出三次。
    XXX.是我没看仔细。对。上面这个想法是正确的，因为每个值全都输出了三次，而我正好开启了三个线程。。这么一看，。上面的问题是我傻逼。

    再次测试：
        public class NumberTest {
            private int i = 100;
            public synchronized void decrement(){
                i--;
            }
            public synchronized int getI(){
                return i;
            }
            public synchronized void print(){
                System.out.println("当前值：" + i);
            }
        }
        public class RunnableTest implements Runnable {
            private static NumberTest numberTest = new NumberTest();
            @Override
            public void run() {
                while (numberTest.getI() >1) {
                    numberTest.decrement();
                    numberTest.print();
                }
            }
            public static void main(String args[]) {
                new Thread(new RunnableTest(),"thread1").start();
                new Thread(new RunnableTest(),"thread2").start();
                new Thread(new RunnableTest(),"thread2").start();
            }
        }
    这样子是可以保证同步的。
----
    上面是对某个方法加锁，下面是对某个代码块加锁
        public class NumberTest {
            private int i = 100;
            public  void decrement(){
                synchronized(this){
                    i--;
                }
            }
            public  int getI(){
                synchronized(this){
                    return i;
                }
            }
            public synchronized void print(){
                synchronized(this){
                    System.out.println("当前值：" + i);
                }
            }
        }
---
    多线程的原子操作。
    对于目前的多线程来说，假设CPU是4核的，那么只能进行4个真正意义上的多线程。所以，目前的多线程应该是cpu在多个线程
间不停地执行、切换，进行的多线程（应该是这样）。那么原子操作就是说，当这个线程没有完成之前，绝对不可以切换线程。
    下面java中的这个类AtomicInteger可以保证线程的原子性（不过我运行了多次，似乎有时候会有点问题，应该是print的问题。）：
        public class NumberTest {
            private AtomicInteger i = new AtomicInteger(100);
            public  void decrement(){
                i.decrementAndGet();
            }
            public  int getI(){
                return i.get();
            }
            public synchronized void print(){
                synchronized(this){
                    System.out.println("当前值：" + i.get());
                }
            }
        }

---
    线程中断
    调用Thread的interrupt()方法，当线程处于非阻塞状态，只是改变了中断状态（实验了，线程正常运行），然后返回true。
    如果此时线程处于阻塞状态，即Thread.sleep()或Object.wait()或Thread.join()，则抛出InterruptException。同时把
中断状态设置回false，即没有被中断。
    也就是说，在一个线程中调用interrupt()方法，真正有影响的是wait()，join(),sleep()方法，以及他们的重载方法。
    通过这个异常，可以进行，比如，当一个线程睡眠时，一旦被中断，抛出异常后，可以在catch中进行相应的处理。
---
    Thread.join()方法
    此方法，可以让一个线程等待另一个线程执行结束后再执行.
    例如，主线程通过调用子线程的join()方法 ，阻塞自己以等待子线程结束。
    也可以通过此方法将两个交替执行的线程合并成为顺序执行的线程。例如在线程B中调用线程A的Join()方法，直到线程A执行完毕后，
才会继续执行线程B。
    上面都是书上说的，让我用人话说一遍。
    就是如果在一个main(主线程)中开启三个子线程，那么当你执行某个子线程的join()方法后，主线程就会被阻塞，直到这个子线程
执行结束（发现自己讲得还不如书。。。）。也就是说，通过这个方法，使得调用这个方法的线程，和被调用该方法的线程成一个顺序结构的
线程，只有被调用该方法的线程执行完毕后，调用该方法的线程才能够继续执行。
    如下：
        public static void main(String args[]) throws InterruptedException {
                Thread thread1 = new Thread(new RunnableTest("线程1"));
                Thread thread2 = new Thread(new RunnableTest("线程2"));
                Thread thread3 = new Thread(new RunnableTest("线程3"));
                thread1.start();
                thread1.join();
                thread2.start();
                thread3.start();
            }
    当主线程main调用子线程thread1的join方法后，结果显示为，当thread1执行结束后，main才继续执行下面thread2和3的start()方法。
    终极一句话，A调用B的join（）方法，相当于一条路上，A，对B说，你他妈的先走。然后A尾随。OK。
    注意：join方法必须放在start方法后。
---
    下面设计一个简单的线程：
        要求，一本稿子，上面有若干段话，胖子每次讲一段话，每次讲中间隔3s。我让胖子开始讲话，然后开始等胖子讲完，
如果等的时间大于10S，就把胖子杀了，然后胖子临死前要说一句，我还会回来的。23333
    如下：
        public class PangZiTest {
            private static String says[] = {"1：我是智障", "2:我脑子有糠", "3：前几天临安地震，其实是我。。。", "4：我的爷爷叫郑星", "5:我的爷爷有大吊"};
            //打印消息 当前线程名 +　消息
            static void printMessage(String message){
                String threadName = Thread.currentThread().getName();
                System.out.format("%s: %s%n",threadName,message);
            }
            //胖子讲话线程 类
            private static class PangZi implements Runnable {
                @Override
                public void run() {
                        for (int i = 0; i < says.length; i++) {
                            printMessage(says[i]);
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                printMessage("我还会回来的");
                                return;//如果不写end，抛出异常后会仍会运行，因为异常已被捕获
                            }
                        }
                }
            }
            public static void main(String args[]) throws InterruptedException {
                printMessage("生出一个pangzi");
                long startTime = System.currentTimeMillis();//开始时间
                Thread t = new Thread(new PangZi(),"胖子");
                t.start();
                while (t.isAlive()){
                    t.join(1000);
                    printMessage("胖子你再说");
                    if((System.currentTimeMillis() - startTime) > 11 * 1000 && t.isAlive()){
                        printMessage("麻痹的，说的贼慢，滚");
                        t.interrupt();
                        t.join();
                    }
                }
                printMessage("END");
            }
        }
---
    死锁：线程同时等待彼此，堵塞。
    **
     * 死锁 例子
     * 两个好友要对对方的拥抱作出同样的回应，都是张开双手，当对方拥抱了自己，再拥抱对方。
     * 当双方同时张开双手，就形成了死锁
     */
    public class DeadLockTest {
        static class Friend{
            private String name;
            public String getName() {
                return name;
            }
            public Friend(String name) {
                this.name = name;
            }
            //张开手，作拥抱动作。
            public synchronized void bow(Friend friend){
                //%s是参数  %n是换行
                System.out.format("%s: %s" + "  拥抱我！%n",this.name,friend.getName());
                friend.bowBack(this);
                //如下写法也一样是死锁：
                //friend.bow(this);
            }
            //回抱对方
            public synchronized void bowBack(Friend friend){
                System.out.format("%s: %s" + " 回抱我!%n",this.name,friend.getName());
            }
        }

        public static void main(String [] args){
            Friend xiaoHua = new Friend("xiaoHua");
            Friend xiaoMing = new Friend("xiaoMing");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xiaoHua.bow(xiaoMing);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xiaoMing.bow(xiaoHua);
                }
            }).start();
        }
    }
    起先我说真的，看不懂。然后百度了下 synchronized。终于明白了。
    这个例子中，小明调用了自己的bow方法，然后在bow中调用了小花的bowBack()方法，而此时，小花正在执行自己的bow()方法，
也企图调用小明的bowBack()方法，这样，两个对象就都在等待对方执行完成各自的bow()方法，然后执行被对方调用的bowBack()方法，
从而陷入了死锁。
    我又试了下，小明在自己的bow()方法中调用小花的bow()方法是同样效果，因为此时，双方都已经在各自的bow()中。

    synchronized注释方法或代码块的时候，该方法或代码块同时只能被一个线程调用。且，每个类实例对应一把锁，每个synchronized方法
必须获得调用该方法的类实例的锁才能继续执行。也就是说，一旦一个线程执行了一个类实例的synchronized方法，它就获得了这个类实例的锁。
    这样同样解释了上面的死锁：小花这个实例的锁被自己的bow()持有，小明实例的锁也被自己的bow()方法持有。所以无法执行各自的bowBack()方法。
    这种机制确保了同一时刻对于每一个类实例，其所有声明为 synchronized 的成员函数中至多只有一个处于可执行状态（因为至多只有一个能够
获得该类实例对应的锁），从而有效避免了类成员变量的访问冲突（只要所有可能访问类成员变量的方法均被声明为 synchronized）。
    不光是类实例，每一个类也对应一把锁，这样我们也可将类的静态成员函数声明为 synchronized ，以控制其对类的静态成员变量的访问。

    当一个线程访问object的一个synchronized(this)同步代码块时，另一个线程仍然可以访问该object中的非synchronized(this)同步代码块。
    对于synchronized(object)，线程进入，则获得该对象锁，那么别的线程在该类所有对象上的任何操作都不能进行.而且这是没有效率的。
    试了下，使用synchronized(this)代码块，这个时候，其他线程再调用this这个实例的其他synchronized方法，是会被阻塞的，但是如果
调用非synchronized方法，则不会阻塞.
    当一个线程访问object的一个synchronized(this)同步代码块时，其他线程对object中所有其它synchronized(this)同步代码块的访问将被阻塞。
    （上面的文字有些是复制的，有些是自己实验了写的，但基本上 ——字字珠玑）
----
    如下代码：
        public class DeadLockTest {
            static class Friend{
                private String name;
                private int age;
                public  void setName1(String name) {
                    synchronized(this.name){
                        this.name = name;
                    }
                }
                public synchronized void setName2(String name) {
                        this.name = name;
                }
                public synchronized String getName() {
                    return name;
                }
                public Friend(String name) {
                    this.name = name;
                }
                //张开手，作拥抱动作。
                public  void bow(Friend friend) throws InterruptedException {
                    synchronized(this.name){
                        System.out.format("%s: %s" + "  拥抱我！%n",this.name,friend.getName());
                        Thread.sleep(10000);
                    }
                }
                //回抱对方
                public synchronized void setNameTest(Friend friend){
                    friend.setName1("100");
                    System.out.println("setName1执行完毕");
                    friend.setName2("100");
                    System.out.println("setName2执行完毕");
                }
            }
            public static void main(String [] args) throws InterruptedException {
                Friend xiaoHua = new Friend("xiaoHua");
                Friend xiaoMing = new Friend("xiaoMing");
                Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            xiaoHua.bow(xiaoMing);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        xiaoMing.setNameTest(xiaoHua);
                    }
                });
                thread1.start();
                thread1.join(1000);
                thread2.start();
            }
        }
        我解释下：上面是小花执行自己的bow()方法，并且睡眠10S，注意，此时该线程获取的锁只是小花.name的锁。
在此期间，小明执行了自己的setNameTest()方法。在该方法中，依次执行小花的setName1()和setName2()方法.
    注意，小花的setName1的同步代码块获取的也是小花.name的锁。而小花的setName2同步代码块获取的是小花这个实例的锁。
    当setNameTest()方法中，先执行setName1方法，也就是试图获取小花.name的锁的时候，该线程被阻塞了。
    而当setNameTest()方法中，先执行setName2方法，也就是试图获取小花实例的锁的时候，成功获取到了，并且接下来执行setName2()
获取小花.name的锁的时候,也成功获取到了。（我本以为是类实例的锁的优先级高于类实例成员变量的锁的优先级，此时bow()应该是被阻塞了的）
    但是刚才试了下，发现，当setName2获取到小花实例的锁后，小花.bow()方法仍在运行，且setName2可以修改name，且当setName2修改了name后，
bow()中的name也就发生了变化。由此，应该可以得出，类实例的锁和类成员变量的锁可以由多个线程同时分别拥有，不会阻塞，且类实例的锁的权限大于
类成员变量的锁，拥有类实例的锁的线程，可以改变已被别的线程锁定了的类成员变量，且会马上发生变化。
    还有一个测试的结果，就是如果一个对象的锁已经被线程A获取，但线程B同样可以获取到那个对象的成员变量的锁，并修改其成员变量(任意成员变量，可以不是获取到的锁的那个)。
---
2017年4月17日 12:43:39
    饥饿，线程一直不能访问共享资源且无法执行，一般发生在对象锁长时间被其他线程霸占，或者优先级调度中，一直被高优先级的线程霸占锁。
    活锁，一个线程不断的去询问另外一个线程的执行结果，而这个“另外的线程”有不断去询问另外的线程的处理结果，就会发生活锁。
    避免饥饿和活锁可以依靠保护块——其原理是，如果多个线程需要做某个事，那么这个事情是有某种条件的，那么在做这个事情之前一直循环这个条件，
重点是，“一直循环这个条件”，而不是简单地判断条件是否成立。更加高效率的做法是，使用Object.wait()方法将当前线程挂起，直到另一线程发起通知。
尽管通知的事件不一定是当前线程等待的事件。（一定要在循环调用wait()事件，不要想当然地认为线程被唤醒后，循环条件一定发生了变化）
    注意，这个循环一定要处于synchronized中。一个线程调用X.wait()时，这个线程必须拥有x的锁（否则会抛出异常）
    wait()方法是，释放锁，并挂起。等待notifyAll通知。notify()只会唤醒被关起中的锁中的随机的某一个。

    写了一个观察者（生产者）模式的小例子。包com.zx.producer,drop对象相当于阻塞队列.
    刚才瞄了下CopyOnWriteArrayList类的源码。发现没有一个synchronized。不过有一个 重入锁对象，上午车上看书的时候瞄到过这个对象。
---
2017年4月17日 15:35:25
    ThreadLocal<T>类可以创造线程私有的变量，只允许当前线程读写。set()方法可以设置泛型的值，get()方法可以获取泛型的值。
    想要让所有线程使用的ThreadLocal<T>类有默认的初始值，需要
            ThreadLocal<String> str = new ThreadLocal<String>(){
                        @Override
                        protected String initialValue() {
                            return "init";
                        }
                    };
    重写它的initialValue()方法.

    ThreadLocal<T>还有一个子类 InheritableThreadLocal<T>,这个类对于子线程来说，是共享的。
    简单地说，当main()创建了两个线程，然后给这两个线程附上自己创建的InheritableThreadLocal,如果此时main()的这个itl是100,
    那么子线程获取到的itl也是100。而如果是使用ThreadLocal，子线程获取的会是null。
---
Compare and swap(比较且交换)CAS算法。是基于cpu硬件的。 CAS是乐观锁：也就是默认线程间不会发生冲突，发生了冲突就重试，直到成功。
    使用情况，如果是非公平锁，每个线程进来都会尝试占有锁，这个尝试过程就是比较且交换。
    如果锁是期望的值（未被占用），就将锁变成占用状态，并返回true，否则，什么都不做。
    java.util.concurrent.atomic包中的一系列原子变量的底层实现就是cpu提供的cas操作。比起自己实现效率快得多。
    原子操作，即该次操作不会被打断，直到执行成功。（事务中也有原子性）。就不会发生多线程数据不一致的情况
    自己实现如下：
        boolean lock = false;
        public synchronized boolean cas(){
            //如果没上锁,获取锁，且返回true
            if(!lock){
                lock = true;
                return true;
            }
            return false;
        }
    atomic实现
        AtomicBoolean lock = new AtomicBoolean(false);
        public boolean cas(){
            return lock.compareAndSet(false,true);
        }
---
2017年4月18日 21:39:19
    AtomicReference<V>这个类有点奇怪，我把Integer当作泛型，创建了一个这个类的实例，然后线程是，循环100次，将这个实例的值，从100
循环到0。然后开两个线程跑，看看能不能同步，结果，是一个线程跑完之后，另一个线程才开始跑。
    还有一点。网上看见的，这个类的set()方法，不是原子的。。。其他都是原子的。但是有个compareAndSet(V expect, V update)。
方法，可以将值替换成想要的值（如果值是期望的话。。有点绕）.
    然后我把AtomicReference<Integer>换成了普通的Integer，就成了期望的结果，两个线程一起跑，很乱。。

    嘿嘿。我果然还是聪明，把线程增加到十多个，结果果然就乱了，之前的结果应该是因为这个操作是原子的，然后，另一个线程一直抢不到cpu，
所以才会发生和串行一样的执行结果吧——私以为。
    原子类，只能保证每个方法的操作是原子操作。但无法保证不发生线程竞态，以及两个原子操作之间，别的线程对值的操作。
---
    重入：当线程已经获取到某个对象的锁之后，可以再次获取。重入的一种实现方法是，为每个锁关联计数器和持有人，当线程A获取到锁后，
计数器+1，持有人为A，此时，其他线程无法获取该锁，而A可以再次获取，然后计数器再+1，如果A释放一层锁，就-1，直到计数器为0，释放锁。
    假设A类，B类继承A类。A类有方法a(),是同步方法，B类重写A类a()方法，但是在方法中，super.a()，如果没有重入，就死锁了。
---
    2017年4月19日 21:01:34
    ExecutorService 执行器服务接口，通过 Executors类 创建实例。主要的实现类如下：
        1.newCachedThreadPool：可根据需要创建新线程的线程池，只会重用空闲并且可用的线程。不限制最大线程数。
            若有空闲线程，则用空闲线程执行任务；若没有，则创建新的线程。空闲任务默认60s自动回收。一般不用它。可能会造成内存溢出。
        2.newWorkStealingPool:创建ForkJoinPool类型的线程池。将一个大任务分割成若干个子任务，并且合并子任务执行的结果。(java8)
             ForkJoinPool(分叉、连接线程池)：
                    work-stealing 工作窃取算法：指某个线程从其他队列里窃取任务来执行。
                        算法使用场景：将一个比较大的任务分成若干相互不依赖的子任务，为了减少线程间的竞争，所以把这些子任务放到不同的队列，
                    为每个队列创建一个单独的线程来执行队列中的任务，线程和队列一一对应。当有线程将自己的任务队列执行完毕后，可以从从其他
                    线程的队列窃取任务来执行。为了减少去窃取任务的线程和被窃取任务的线程之间的竞争，通常使用双端队列。被窃取线程永远从头部获取
                    任务，去窃取任务线程从尾部获取。
                        优点：减少线程间的竞争。缺点是，某些情况下还存在竞争，比如双端队列中只有一个任务的时候。并且还消耗了更多的系统资源，来创建
                    多个线程和多个双端队列。
             ForkJoinTask是一个抽象类
                 RecursiveAction:ForkJoinTask抽象子类，用于返回没有结果的任务
                 RecursiveAction：抽象子类，用于返回有结果的任务。
             写了一个ForkJoinPool的例子。fork()是分配子任务,应该相当于执行线程，join()是获取执行结果
        3.newSingleThreadExecutor:一个单线程化的线程池，用唯一的工作线程执行任务，确保所有任务按指定顺序执行（FIFO,LIFO,优先级）。
            试了下，其他线程池执行线程的确不是依次的。
        4.newFixedThreadPool:创建一个可重用的固定线程数的线程池，以共享的无界队列（不设置初始size的队列）方式来运行这些线程。
            能同时运行的个数就是new它时设定的个数。其他线程想要运行，必须等运行的线程数于设定值执行结束，才能再次调用它的execute(否则不运行)
        5.newScheduledThreadPool:创建一个可以重复执行任务的线程池，并可以指定任务的间隔和延迟时间。也是有固定的数量的线程池。类似Timer
            方法如下：
                1.schedule(Callable 线程,long 延时时间,TimeUnit 时间单位) 延时执行一次线程
                2.schedule(Runnable,long,TimeUnit)同上，不过返回的future.get()返回的是NUll
                3.scheduleAtFixedRate(Runnable,long 首次延时时间,long 周期间隔时间,TimeUnit) 周期执行任务，不受每次任务执行时间的影响，到时就执行。
                4.scheduleWithFixedDelay(Runnable,long,long,timeunit) 同上，不过是每次任务执行完了在开始计算周期间隔时间。
        6.newSingleThreadScheduledPool:只有一个线程，可以重复定时执任务的线程池。

    也可以自己直接 new ThreadPoolExecutor()  上面所有的方法，基本上都是new这个线程池对象，传递了不同的参数而已。
                int corePoolSize = 5;//核心线程大小
                int maxPoolSize = 10;//最大线程数
                long keepAliveTime = 5000;
                ExecutorService executorService = new ThreadPoolExecutor(
                        corePoolSize,//核心线程数
                        maxPoolSize,//最大线程数
                        keepAliveTime,//空闲线程存活时间
                        TimeUnit.MILLISECONDS,//存活时间单位
                        Queue<Runnable>()//队列
                        ThreadFactory //线程工厂
                        //handle类d,当核心线程、队列、最大线程都满了，就使用这个类处理任务，可以使用JDK提供的拒绝策略，也可以自定义拒绝策略。
                        RejectedExecutorHandler handler

                );
                关于队列这个参数：
                    使用有界队列时，优先级如下：
                        其中，线程放入的优先级是   核心线程  --》 队列  --》 最大线程 --》handle类
                        也就是，先将核心线程塞满，超过后放入队列，队列满了就多开启几个线程，让线程数达到最大线程--》再超出就使用handle类处理。
                        添加一个新的任务时，如果没有指定handle类，而核心线程、队列、最大线程又都满了，则抛出Reject异常。
                    使用无界队列时：
                        除非系统资源耗尽，否则不会有任务创建失败的情况。
                        而且最大线程数相当于没有，如果核心线程数满了，新任务就会存入队列，直到系统资源耗尽。

    JDK拒绝策略：（当任务满了需要拒绝的策略）
        AbortPolicy:直接抛出异常，系统继续正常工作。
        CallerRunsPolicy:只要线程没有关闭，该策略直接在调用者线程中，运行当前被拒绝的任务.
        DiscardOldestPolicy:丢弃最老的一个请求，尝试再次提交当前任务。当前任务替换掉最老的未执行的任务。
        DiscardPolicy:丢弃无法处理的任务，不给予任何处理。
        如果需要自定义拒绝策略，可以实现RejectedExecutionHandler接口：
            在需要重写的rejectedExecution(Runnable runnable,ThreadPoolExecutor executor)
            有需要执行的任务和当前的线程池对象。
            一般的自定义拒绝策略都是记录日志，而不是另外存到缓存中。因为如果内存够，倒不如不用拒绝，
            直接扩充队列；要是内存不够了，就是想存进缓存都做不到了。

    ExecutorService接口提供如下方法：
        1.execute(Runnable):普通的执行一个线程。
        2.submit(Runnable):会返回一个future对象，可以通过该对象查看线程适度执行完毕，如果正确执行(捕获了异常也算) future.get()返回 null
            只要是使用Runnable接口，就算是返回Future对象，使用future.get()方法，获取到的也只会是null
        3.submit(Callable):与submit(Runnable)类似。Runnable.run()执行异步。Callable.call()执行异步，返回执行结果<T>(future)
        4.invokeAny():有一类线程执行方式如下：启动若干个独立的线程去计算一个结果，当任意一个线程得到结果后，立刻终止所有线程。
            这个方法就是为此设计，参数是一个List,list中的每个元素必须实现Callable接口。功能是依次启动list中的线程，并将第一个得到的
            结果作为返回值，然后立刻终止所有的线程。invokeAny是一个同步方法。
        5.invokeAll():将所有Callable对象都执行，并且返回一个list<Future>对象。它也是同步的。
        6.shutdown()：调用后，所有线程都会停止运行，但不是立即的，而是等到已提交的任务才会停止，在执行当前任务时，不再接受新的任务。
        7.shutdownNow():立即停止所有线程。当线程处在while(true)中，无法停止
        9.awaitTermination():阻塞自己，等待所有子线程执行完毕

    Future接口：
        表示一个异步计算的结果。提供了检查计算事都完毕的方法，等待计算完毕的方法，返回计算结果的方法。当计算完毕的时候，可以用get()
        获取计算结果，不过它是一直阻塞的，知道有结果返回。cancel()可以取消任务执行，也有判断任务是否被取消的方法。如果不需要返回结果，
        只需要一个可以取消的任务，可以声明Future<?>并在Callable中返回null.
        有一个isDone()方法，可以判断当前它是否完成。
        如果调用get()方法时，线程还没有结束，返回对应的值，那么调用get()方法的线程就会阻塞。
        FutureTask Future实现类: 它同时继承Future接口和Runnable接口。
            写了个future的简单实现。
            主函数（Main类）创建 请求对象(FutureClient类)（类似XMLHttpRequest），创建请求参数，发起请求（client.request()方法）（类似Ajax）.
            futureClient.request()方法：创建一个代理对象(FutureData)，创建新的线程，发起异步请求，返回一个代理对象（返回时是空的）。
            发起异步请求的步骤是 创建一个真实返回的对象(RealData),创建时，这个对象就自己去查询数据了（相当于这个新的线程被阻塞了，反正就是去做事了）。
            最后调用代理对象的getResult方法，在这个方法中，调用真实对象的getResult方法，如果，此时真实对象获取到了数据，那么会直接返回，
            如果没有，那么这个getResult()会被阻塞，直到对象获取完毕。
        ****!!!****
        FutureTask则是一个RunnableFuture<V>，即实现了Runnbale又实现了Futrue<V>这两个接口，另外它还可以包装Runnable和Callable<V>，
        所以一般来讲是一个符合体了，它可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行，并且还可以通过v get()返回执行结果，
        在线程体没有执行完成的时候，主线程一直阻塞等待，执行完则直接返回结果。

    Semaphore类：信号量。线程同步辅助类，维护当前访问自身的线程个数，例如，实现一个文件允许的并发访问数。相当于分配出去x把锁。
        acquire():从信号量中获取一个许可，在获得之前一直被阻塞。否则线程被中断。
        release():释放一个许可。
        availablePermits():返回当前可用的信号量数目。
        hasQueuedThreads():查询当前是否有线程在等待获取。
        单个信号量的限制可以实现互斥锁，解决死锁问题。
        可以使用它进行限流。

    下面自定义一个线程执行器。。就懒得写了。
        思想就是 一个Task类，继承Runnable接口，多加setId和getId的方法。
        一个service接口，方法就写的和其他ExecutorService一样就好了。
        然后一个service实现类，继承一个ExecutorService类，且实现上面那个service接口。
---
2017年4月21日 11:37:01 下次写项目总结，试下用md写。markdown
    --并发集合
        --阻塞队列（FIFO,先入先出）和阻塞栈（后入先出LIFO）：
            线程安全的队列，当队列满的时候，生产线程将会被阻塞，直到消费线程从里面取出数据。当队列空的时候，消费线程将会阻塞，直到
            生产线程放入数据。注意，同一时间阻塞队列只能有一个线程操作队列。

            方法：
                            抛出异常   返回特殊值  一直阻塞    超时退出
                插入方法：    add()      offer(e)    put()     offer(e,time,unit)
                移除方法：    remove()   poll()      take()    poll(time,unit)
                检查方法：    element()  peek()
                    remove()移除并返回，如果为空，抛出异常
                    element()返回队列头部的元素，如果为空，抛出异常。
                    peek()返回队列头部的元素，如果为空，返回null
                    -抛出异常：当阻塞队列满或空的时候，再往队列里插入或取出元素，会抛出异常
                    -返回特殊值：插入如果成功，返回true；取出方法如果没有，返回null
                    -一直阻塞：如果满了或者空的，就阻塞自己，等待。
                    -超时退出：阻塞一定的时间，如果超时，退出。
            实现类：
                1.ArrayBlockingQueue,最常用，有界的可指定FIFO或LIFO阻塞队列。
                    可以选择是否需要公平性，如果公平参数被设置为 true，等待时间最长的线程会优先得到处理（其实就是通过将
                    ReentrantLock 设置为 true 来达到这种公平性：即等待时间最长的线程会先操作）
                    内部有一个定长数组，以便缓冲队列中的数据对象。没有读写分离。生产者消费者不能并行
                    适合高并发场景，指定界限、
                2.LinkedBlockedQueue,与ArrayBlockingQueue几乎一模一样,但array数组结果，它是链式，且无界，也可以有界。
                    容量在不指定的情况下为 Integer.MAX_VALUE，但是也可以指定其最大容量
                    也有一个定长链表，实现数据缓冲。内部读写分离（读和写两个锁），从而是生产者消费者可以并行。
                    适合中并发场景，确定了处理的数据不会过多。据我自己理解，单纯的插入和删除这个应该是比ArrayBlockingQueue快的（实验了下，是的）。
                3.SynchronousQueue:没有缓冲的队列，生产者数据会被消费者直接获取并消费。
                    只有当有线程尝试取元素，并被阻塞的时候，才能往这个队列中添加数据。
                    相当于一个虚拟队列，也无法往里面添加元素，而是直接把生产者推送过来的数据推送给消费者。
                    适合没什么任务的场景。
                4.PriorityBlockingQueue，一个带优先级的队列，不是FIFO，元素按照优先级被移除，也是没有容量限制的。
                    存储的对象必须实现Comparable接口，通过这个接口的方法进行比较确认优先级。
                    只有再每次取出元素的时候，会使用选择排序，取出优先级最高的元素。
                5.DelayQueue：存放Delayed元素的无界阻塞队列。其实现原理也是Comparable接口。
                    只有超时后才能取出数据。该队列头部是超时时间最久的Delayed元素
                    如果没有元素超时，则相当于该队列为空。
                    getDelay()方法等于或小于0，表示队列已经有元素超时了，就可以取出元素了。
                    这个队列中的元素必须实现Delay接口。
                    没有大小限制。
                    应用场景：移除缓存超时数据，任务超时处理，空闲连接关闭等。

        ConcurrentMap：并发map对象，被ConcurrentHashMap实现。ConcurrentHashMap读写时不会锁住整个map，只会锁住要写的那部分。



---
2017年4月22日 03:40:03  网吧通宵打代码。。。
    synchronized使用的是可重入锁，并且不是公平锁。
    java.util.concurrentLocks包提供了更复杂的锁。
    Lock接口：所有锁的公公接口。
        lock():获取锁，如果锁已被其他线程获取，阻塞。
        tryLock():获取锁，如果锁没被其他线程获取，返回true，并获取锁。否则返回false,也可以等待一定的时间。
        unLock()：释放锁。
        isFair()；是否是公平锁。
        isLocked():是否锁定.
        getHoldCount():获取当前线程保持此锁的个数，也就是调用lock()且未调用unLock()的次数（lock-unlock）。
        lockInterruptibly():优先响应中断的锁。
        getQueueLength():返回正在等待获取该锁的线程数。
        getWaitQueueLength():返回正在等待被唤醒的线程数
        hasQueuedThread(Thread):查询指定线程是否在等待此锁。
        hasQueuedThreads():查询是否有线程在等待此锁。
        hasWaiters():查询是否有正在等待被唤醒的线程。
        一般使用这些锁，都需要try/finally，try中获取锁，finally中释放锁。
        ReentrantLock:可重入锁，互斥锁。
            支持设定成公平（先到先得）或非公平（每个锁都尝试获取，具体哪个获取到看运气）锁，构造函数中传入（true）为公平锁
            默认为非公平。非公平效率高、
            假设获取了x次锁，就需要调用x次unLock()方法释放锁。（之前的死锁例子可以通过它避免。）

        ReadWriteLock接口,
            ReentrantReadWriteLock:ReadWriteLock接口实现类。
            维护了一对锁。一个用于只读操作，一个用于写入操作。
            如果没有写入，读锁可以由多个线程同时占有。写入锁是独占的，一次只能有一个线程占用。
            读写锁允许对共享数据更高级别的并发访问。如果数据对象的写入比较少，读取比较多，使用它可以提升效率。
            如果写入操作 比较多，那么这个锁得不偿失，因为读写锁本身就比写入锁复杂。
            它允许写入锁降级（重入）为读取锁（无法升级）：
                实现方式是，先获取写入锁，然后获取读取锁，最后释放写入锁。
                不支持升级是因为数据可见性的考虑。假如有先获取了读取锁，然后获取写入锁，在这个时候，由于读取锁是可以公用的，其他线程可能
                也获取着读锁，如果此时这个获取到写锁的线程修改了数据，对于其他读取锁，数据就不可见了。（这个原因是我自己的理解。）

            Condition：条件 lock.newCondition()方法创建
                将Object的监听器方法（wait，notify,notifyAll）分解成不同的对象，以便通过这些对象于任意的Lock实现组合使用。
            为每个对象提供多个set（wait-set）方法。其中。lock替代了synchronized的使用,Condition替代了监听器方法的使用。
                条件为线程提供了一个含义，以便在某个状态条件现在可能true的另一个线程通知它之前，一直挂起该线程。

                反正就是用这个类实现各种监听器方法。
                    await替代wait，signal替代notify,signalAll替代notifyAll。
                然后它是被绑定到Lock上的，要创建一个Lock的Condition要使用newCondition()方法。
                它和普通的监听器的区别就在于，它可以为多个线程间建立不同的Condition。
                也就是一个lock可以创建多个Condition。
                差不多就是A线程使用ConditionA.await()来阻塞自己，B线程使用ConditionB.await()来阻塞自己，
                C线程使用ConditionC.await()来阻塞自己，那么在A线程中调用ConditionB.signal()，就肯定会唤醒
                B线程，而不是唤醒C线程，而对于普通的notify()方法来说，只会唤醒任意一个线程。
---
2017年4月28日 21:20:17
    并发类容器。

    Vector和HashTable都是过于古老的同步容器。虽然同步了，但是并发性（性能）很差.
    目前jdk5使用concurrent包中的ConcurrentHashMap代替HashTable，使用CopyOnWriteArrayList代替Vector。
    以及高并发队列ConcurrentLinkedQueue，或阻塞队列LinkedBlockedQueue.

    --ConcurrentMap：有两个重要实现
        ConcurrentHashMap:
            内部使用段（Segment）表示不同的部分，每个段就是一个小的HashTable。它们有自己的锁，
        只要多个修改操作发生在不同的段上，就可以并发进行。把一个整体分成了16个段，也就是最高支持16个线程
        的并发修改操作。这也是在多线程场景时减小锁的粒度从而降低锁竞争的一种方案，并且大多数共享
        变量使用volatile声明。
        ConcurrentSkipListMap:(支持并发排序，类似treeMap)

    --CopyOnWrite:也有两个实现
        它是写时复制的容器。就是当我们往一个容器中添加元素的时候，不直接往当前容器中添加，
        而是先将当前容器拷贝，复制出新的容器，然后往新的容器中添加元素。
        添加完元素后，把原容器的引用指向新的容器。这样做的好处是，可以对容器进行
        并发的读，而不需要加锁。因为当前容器不会添加任何元素。
        所以，这种容器也是一种读写分离的思想，读和写不同的容器。
        所以通常在读多写少的情况下使用。
        CopyOnWriteArrayList:
        CopyOnWriteArraySet:


    并发Queue

    --ConcurrentLinkedQueue:
        高并发场景下的无锁队列，性能好于BlockQueue,基于链接节点的无界的线程安全的队列。FIFO，不允许NULL元素。
    --BlockingQueue:上面已经写了。补充上去了。
    
    
    Deque:双向队列。可以同时从头或者尾存取数据。
        具体实现有：LinkedBlockingDeque、ArrayDeque(无界)、LinkedList.
        
---
    AQS: AbstractQueuedSynchronizer. Concurrent包中最复杂的一个类，绝大部分书中都不会提及它。。。所以我之前听都没听过。。
        抽象的队列式的同步器，AQS定义了一套多线程访问共享资源的同步器框架
        在继承体系中,locks(reentrantLock/ReadWriteLock)、CountDownLatch、semaphore（信号量)等，都继承它。

        它维护了一个volatile int 的变量state（代表共享资源）和一个FIFO阻塞队列（多线程争用资源被阻塞时会进入此队列）。
            state的访问方式有三种:
                getState()
                setState()
                compareAndSetState()


---
    AtomicMarkableReference类:
        描述的一个<Object,Boolean>的对，可以原子的修改Object或者Boolean的值，这种数据结构在一些缓存或者状态描述中比较有用。
        这种结构在单个或者同时修改Object/Boolean的时候能够有效的提高吞吐量。
    AtomicStampedReference类:
        维护带有整数“标志”的对象引用，可以用原子方式对其进行更新。
        对比AtomicMarkableReference类的<Object,Boolean>，AtomicStampedReference维护的是一种类似<Object,int>的数据结构，
        其实就是对对象（引用）的一个并发计数。但是与AtomicInteger不同的是，此数据结构可以携带一个对象引用（Object），
        并且能够对此对象和计数同时进行原子操作。
---
    指令重排序
        Java语言规范规定了JVM线程内部维持顺序化语义，也就是说只要程序的最终结果等同于它在严格的顺序化环境下的结果，
        那么指令的执行顺序就可能与代码的顺序不一致。这个过程通过叫做指令的重排序。指令重排序存在的意义在于：
        JVM能够根据处理器的特性（CPU的多级缓存系统、多核处理器等）适当的重新排序机器指令，使机器指令更符合CPU的执行特点，最大限度的发挥机器的性能。

    Happens-before(碰撞-之前):
        Java存储模型有一个happens-before原则，就是如果动作B要看到动作A的执行结果（无论A/B是否在同一个线程里面执行），那么A/B就需要满足happens-before关系。
        在介绍happens-before法则之前介绍一个概念：JMM动作（Java Memeory Model Action），
            Java存储模型动作。一个动作（Action）包括：变量的读写、监视器加锁和释放锁、线程的start()和join()。
        happens-before完整规则：
        （1）同一个线程中的每个Action都happens-before于出现在其后的任何一个Action。
        （2）对一个监视器的解锁happens-before于每一个后续对同一个监视器的加锁。
        （3）对volatile字段的写入操作happens-before于每一个后续的同一个字段的读操作。
        （4）Thread.start()的调用会happens-before于启动线程里面的动作。
        （5）Thread中的所有动作都happens-before于其他线程检查到此线程结束或者Thread.join（）中返回或者Thread.isAlive()==false。
        （6）一个线程A调用另一个另一个线程B的interrupt（）都happens-before于线程A发现B被A中断（B抛出异常或者A检测到B的isInterrupted（）或者interrupted()）。
        （7）一个对象构造函数的结束happens-before与该对象的finalizer的开始
        （8）如果A动作happens-before于B动作，而B动作happens-before与C动作，那么A动作happens-before于C动作。

    volatile语义
        （1）Java 存储模型不会对volatile指令的操作进行重排序：这个保证对volatile变量的操作时按照指令的出现顺序执行的。
        （2）volatile变量不会被缓存在寄存器中（只有拥有线程可见）或者其他对CPU不可见的地方，每次总是从主存中读取volatile变量的结果。
            也就是说对于volatile变量的修改，其它线程总是可见的，并且不是使用自己线程栈内部的变量。
            也就是在happens-before法则中，对一个volatile变量的写操作后，其后的任何读操作理解可见此写操作的结果。
---
    Master-Worker:常用的工作模式，核心思想是由两类进程协作工作：Master进程和Worker进程，
        Master负责接收和分配任务，Worker负责处理子任务。当各个Worker子进程处理完后，会将结果返回给
        Master进程。也就是将一个大任务分解成若干个小任务，并行执行。（应该就是forkJoin模式）
    已经写了一个小例子。不过这样看来，这个模式好像不同于fork-join。
    这个模式是多个子线程并发从所有任务队列中获取随机任务，然后执行。
    而forkJoin模式是，根据任务规模，动态分配子线程，然后每个线程执行自己分配到的任务。
---
    Concurrent.util:
        CountDownLatch:一个辅助类，和join()类似，但它用来管理一组线程。有一个初始值，就是计数器。
                    其原理是：设定一组计数器，每个线程完成的时候，就将计数器-1，如果计数器为0，就释放锁，线程就能继续进行.
                    功能也就是，管理一组子线程，让它成为每个线程的成员变量，然后在子线程的构造函数中将它作为参数传给子线程。
                    countDown()方法，线程调用该方法，可以将计数器-1.
                    使用await()方法，阻塞自己，等待所有管理的线程执行完毕(计数器为0时)释放。可以设定等待时间。

        CyclicBarrier:屏障（可以设置多个）,多个线程独立运行，完成任务的线程就等待其他线程，当所有线程完成任务后，所有线程再一起运行。
                    可以设置屏障拦截的线程个数，当达到X个数，就释放屏障。也可以设置等待的超时时间。
                    还可以设置屏障释放时执行的线程，也就是释放线程时，执行另外的线程。
                    假设它的初始化线程个数是2，那么当A线程调用CyclicBarrier.await()方法时，阻塞自己等待，
                        当B线程也调用await(),则，两个线程都可以继续执行了。
                    调用它的reset()方法还可以继续使用，countDownLatch是一次性的。

        CountDownLatch 和 CyclicBarrier区别：
                    countDownLatch：一个线程（或多个），等待另外N个线程完成某件事情之后进行。
                    cyclicBarrier:N个线程相互等待，任何一个线程完成之前，其他线程必须相互等待。而且它可以多次在某个点等待。

        ThreadLocalRandom:线程安全的随机数.
                    先调用ThreadLocalRandom.current()，然后调用.nextInt(x,y)生成随机数,大于等于x,小于Y。

        Exchanger:在两个线程间交换数据，且只能是两个线程。它提供了一个同步点。在这个同步点，两个线程可以交换数据。
                    通过Exchange()方法提供数据给其他线程，并接受其他线程提供的数据。
---
    异常处理 http://www.blogjava.net/xylz/archive/2013/08/05/402405.html
        通常java.lang.Thread对象运行设置一个默认的异常处理方法：
            java.lang.Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler)
            当然，我们可以覆盖此默认实现，只需要一个自定义的java.lang.Thread.UncaughtExceptionHandler接口实现即可。
        
        而在线程池中却比较特殊。默认情况下，线程池 java.util.concurrent.ThreadPoolExecutor 会Catch住所有异常，
            当任务执行完成(java.util.concurrent.ExecutorService.submit(Callable))获取其结果时，
            (java.util.concurrent.Future.get())会抛出此RuntimeException。
            
    java.util.concurrent.ThreadPoolExecutor 预留了一个方法，运行在任务执行完毕进行扩展（当然也预留一个protected方法beforeExecute(Thread t, Runnable r)）：
        protected void afterExecute(Runnable r, Throwable t) { } 
        此方法的默认实现为空，这样我们就可以通过继承或者覆盖ThreadPoolExecutor 来达到自定义的错误处理。

---
    Disruptor:类似一个性能贼快的队列。生产者生产出的数据放到这里面，它会主动推送给消费者。内部大量缓存，无锁。
    一般来说，只要需要在多个线程之间交换数据，就可以使用这个框架。
    轻量的JMS，也可以说是一个观察者模式的实现，或者事件监听模式的实现。
    RingBuffer:(大小需要是2^n次方)这个框架中存储数据的对象，是一个环形的结构，发布的时候就是获取到下一个索引（sequence），索引一直指向下一个空元素。
    然后获取到下一个索引对应的数据对象，然后填充数据，然后发布相应的索引出去就可以了。
    索引对应的位置的计算方法是 索引%ringBufferSize (索引对长度取模)，余X，就从第一个区域往后数x个单位，也就是目前的数据位置。
    照着写了一个简单的例子，一个Disruptor需要：
        1.单个数据类(LongEvent)：就是需要生产/消费的数据类型，例如String，也可以自定义。
        2.数据工厂(LongEventFactory)：创建空的（应该也可以初始化一些属性）数据类对象.
        3.数据处理类(LongEventHandler):差不多就是消费者，负责处理发布了的数据
        4.生产者（LongEventProducer）：模拟的生产者，获取ringBuffer（disruptor存放数据的对象）中的空对象，放入数据，然后发布。

    等待策略:
        1.Disruptor默认的等待策略是BlockingWaitStrategy。这个策略的内部适用一个锁和条件变量来控制线程的执行和等待（Java基本的同步方法）。
            BlockingWaitStrategy是最慢的等待策略，但也是CPU使用率最低和最稳定的选项。
            然而，可以根据不同的部署环境调整选项以提高性能。
        2.SleepingWaitStrategy:SpleepingWaitStrategy的CPU使用率也比较低。它的方式是循环等待并且在循环中间调用LockSupport.parkNanos(1)来睡眠，
            （在Linux系统上面睡眠时间60µs）.然而，它的优点在于生产线程只需要计数，而不执行任何指令。
            并且没有条件变量的消耗。但是，事件对象从生产者到消费者传递的延迟变大了。
            SleepingWaitStrategy最好用在不需要低延迟，而且事件发布对于生产者的影响比较小的情况下。比如异步日志功能。
        3.YieldingWaitStrategy:YieldingWaitStrategy是可以被用在低延迟系统中的两个策略之一，这种策略在减低系统延迟的同时也会增加CPU运算量。
            YieldingWaitStrategy策略会循环等待sequence增加到合适的值。循环中调用Thread.yield()允许其他准备好的线程执行。
            如果需要高性能而且事件消费者线程比逻辑内核少的时候，推荐使用YieldingWaitStrategy策略。例如：在开启超线程的时候。
        4.BusySpinWaitStrategy:BusySpinWaitStrategy是性能最高的等待策略，同时也是对部署环境要求最高的策略。
            这个性能最好用在事件处理线程比物理内核数目还要小的时候。例如：在禁用超线程技术的时候。

    这个框架可以使用Disruptor类进行操作，也可以直接使用RingBuffer类进行操作。但是注入下面有执行顺序条件的还是需要Disruptor类的。
        如果只是多个生产者消费者，不需要Disruptor.
    这个框架也可以进行复杂的操作，例如P1生产出，给C1、C2消费，C1、C2消费完成后C3消费。
    
    ！！！这个框架可以实现分布式事务，也就是从队列中取出任务，然后，由两个handler同时处理，只有这两个handle都成功，才能执行下一个handler。
    那么这两个handler可以同时调用soa，如果成功，返回ok，失败，返回falid。然后这两个都执行完毕后，再下一个handler中，就可以判断是否都执行成功，
    然后进行提交或回滚。
    
    

