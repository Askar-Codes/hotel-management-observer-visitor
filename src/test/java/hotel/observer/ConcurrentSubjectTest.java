package hotel.observer;

import hotel.events.GuestCheckedIn;
import hotel.events.GuestCheckedOut;
import hotel.testsupport.ParallelTaskRunner;
import hotel.testsupport.RecordingObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentSubjectTest {

    private ConcurrentSubject<Event> subject;
    private final Event checkIn = new GuestCheckedIn("Anna", "101", "Floor 1");
    private final Event checkOut = new GuestCheckedOut("Anna", "101", "Floor 1");

    @BeforeEach
    void createEmptySubject() {
        subject = new ConcurrentSubject<>();
    }

    @Test
    void observerReceivesEventsOfTheTypeItSubscribedTo() {
        RecordingObserver observer = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, observer);

        subject.publishEventToAllSubscribersOfItsType(checkIn);

        assertEquals(List.of(checkIn), observer.getReceivedEvents());
    }

    @Test
    void observerDoesNotReceiveEventsOfOtherTypes() {
        RecordingObserver observer = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, observer);

        subject.publishEventToAllSubscribersOfItsType(checkOut);

        assertEquals(0, observer.countReceivedEvents());
    }

    @Test
    void unsubscribedObserverReceivesNothingMore() {
        RecordingObserver observer = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, observer);
        subject.publishEventToAllSubscribersOfItsType(checkIn);

        subject.unsubscribe(EventType.GUEST_CHECKED_IN, observer);
        subject.publishEventToAllSubscribersOfItsType(checkIn);

        assertEquals(1, observer.countReceivedEvents());
    }

    @Test
    void observersAreCalledInTheOrderTheySubscribed() {
        List<String> callOrder = new CopyOnWriteArrayList<>();
        subject.subscribe(EventType.GUEST_CHECKED_IN, event -> callOrder.add("first"));
        subject.subscribe(EventType.GUEST_CHECKED_IN, event -> callOrder.add("second"));
        subject.subscribe(EventType.GUEST_CHECKED_IN, event -> callOrder.add("third"));

        subject.publishEventToAllSubscribersOfItsType(checkIn);

        assertEquals(List.of("first", "second", "third"), callOrder);
    }

    @Test
    void observerCanUnsubscribeItselfInsideItsOwnHandler() {
        List<Event> received = new ArrayList<>();
        Observer<Event> oneShotObserver = new Observer<>() {
            @Override
            public void handleEvent(Event event) {
                received.add(event);
                subject.unsubscribe(EventType.GUEST_CHECKED_IN, this);
            }
        };
        subject.subscribe(EventType.GUEST_CHECKED_IN, oneShotObserver);

        subject.publishEventToAllSubscribersOfItsType(checkIn);
        subject.publishEventToAllSubscribersOfItsType(checkIn);

        assertEquals(1, received.size());
    }

    @Test
    void observerSubscribedInsideAHandlerOnlySeesLaterEvents() {
        RecordingObserver newcomer = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, event -> subject.subscribe(EventType.GUEST_CHECKED_IN, newcomer));

        subject.publishEventToAllSubscribersOfItsType(checkIn);
        assertEquals(0, newcomer.countReceivedEvents());

        subject.publishEventToAllSubscribersOfItsType(checkIn);
        assertEquals(1, newcomer.countReceivedEvents());
    }

    @Test
    void fourThreadsPublishingOneThousandEventsEachDeliverExactlyFourThousandEvents() throws Exception {
        RecordingObserver observer = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, observer);

        ParallelTaskRunner.runTaskOnEachOfThreads(4, threadNumber -> {
            for (int eventNumber = 0; eventNumber < 1000; eventNumber++) {
                subject.publishEventToAllSubscribersOfItsType(checkIn);
            }
        });

        assertEquals(4 * 1000, observer.countReceivedEvents());
    }

    @Test
    void observersCanJoinAndLeaveWhileFourThreadsArePublishing() throws Exception {
        RecordingObserver permanentObserver = new RecordingObserver();
        subject.subscribe(EventType.GUEST_CHECKED_IN, permanentObserver);

        // threads 0..3 publish 1000 events each, thread 4 keeps subscribing and unsubscribing another observer
        ParallelTaskRunner.runTaskOnEachOfThreads(5, threadNumber -> {
            if (threadNumber < 4) {
                publishOneThousandCheckIns();
            } else {
                subscribeAndUnsubscribeManyTimes();
            }
        });

        assertEquals(4 * 1000, permanentObserver.countReceivedEvents());
    }

    private void publishOneThousandCheckIns() {
        for (int eventNumber = 0; eventNumber < 1000; eventNumber++) {
            subject.publishEventToAllSubscribersOfItsType(checkIn);
        }
    }

    private void subscribeAndUnsubscribeManyTimes() {
        RecordingObserver temporaryObserver = new RecordingObserver();
        for (int round = 0; round < 2000; round++) {
            subject.subscribe(EventType.GUEST_CHECKED_IN, temporaryObserver);
            subject.unsubscribe(EventType.GUEST_CHECKED_IN, temporaryObserver);
        }
    }
}
