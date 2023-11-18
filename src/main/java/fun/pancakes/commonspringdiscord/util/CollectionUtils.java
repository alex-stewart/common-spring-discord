package fun.pancakes.commonspringdiscord.util;

import java.util.*;

public class CollectionUtils {

    private static final Random random = new Random();

    public static <E> E getRandomArrayElement(E[] array) {
        return Arrays.stream(array).skip(random.nextInt(array.length)).findFirst().orElse(null);
    }

    public static <E> E getRandomSetElement(Set<E> set) {
        return set.stream().skip(random.nextInt(set.size())).findFirst().orElse(null);
    }

    public static <E> List<E> shuffleList(List<E> list) {
        List<E> shuffledList = new ArrayList<>(list);
        Collections.shuffle(shuffledList, random);
        return shuffledList;
    }

}
