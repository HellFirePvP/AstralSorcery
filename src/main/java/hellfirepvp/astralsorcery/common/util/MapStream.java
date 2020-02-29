/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.Tuple;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MapStream
 * Created by HellFirePvP
 * Date: 09.11.2019 / 13:09
 */
public class MapStream<K, V> implements Stream<Tuple<K, V>> {

    private Stream<Tuple<K, V>> decorated;

    private MapStream(Stream<Tuple<K, V>> decorated) {
        this.decorated = decorated;
    }

    public static <K, V> MapStream<K, V> of(Map<K, V> map) {
        return new MapStream<>(map.entrySet()
                .stream()
                .map(e -> new Tuple<>(e.getKey(), e.getValue())));
    }

    public static <K, V> MapStream<K, V> of(Collection<Tuple<K, V>> tplCollection) {
        return new MapStream<>(tplCollection.stream());
    }

    public static <K, V> MapStream<K, V> of(Stream<Tuple<K, V>> tplStream) {
        return new MapStream<>(tplStream);
    }

    public static <K, V> MapStream<K, V> ofKeys(Collection<K> collection, Function<K, V> valueProvider) {
        return ofKeys(collection.stream(), valueProvider);
    }

    public static <K, V> MapStream<K, V> ofKeys(Stream<K> stream, Function<K, V> valueProvider) {
        return new MapStream<>(stream.map(k -> new Tuple<>(k, valueProvider.apply(k))));
    }

    public static <K, V> MapStream<K, V> ofValues(Collection<V> collection, Function<V, K> keyProvider) {
        return ofValues(collection.stream(), keyProvider);
    }

    public static <K, V> MapStream<K, V> ofValues(Stream<V> stream, Function<V, K> keyProvider) {
        return new MapStream<>(stream.map(v -> new Tuple<>(keyProvider.apply(v), v)));
    }

    public static <K, V> void forEach(Map<K, V> map, Consumer<? super Tuple<K, V>> forEachFn) {
        of(map).forEach(forEachFn);
    }

    public Map<K, V> toMap() {
        return decorated.collect(Collectors.toMap(Tuple::getA, Tuple::getB));
    }

    public <R> List<R> toList(BiFunction<K, V, R> flatFunction) {
        return decorated.map(tpl -> flatFunction.apply(tpl.getA(), tpl.getB())).collect(Collectors.toList());
    }

    public List<Tuple<K, V>> toTupleList() {
        return decorated.collect(Collectors.toList());
    }

    public <R> MapStream<K, R> mapValue(Function<V, R> valueMapper) {
        return of(decorated.map(tpl -> new Tuple<>(tpl.getA(), valueMapper.apply(tpl.getB()))));
    }

    public <R> MapStream<R, V> mapKey(Function<K, R> keyMapper) {
        return of(decorated.map(tpl -> new Tuple<>(keyMapper.apply(tpl.getA()), tpl.getB())));
    }

    public <R> Stream<R> flatten(BiFunction<K, V, R> flatFunction) {
        return decorated.map(tpl -> flatFunction.apply(tpl.getA(), tpl.getB()));
    }

    @Override
    public Stream<Tuple<K, V>> filter(Predicate<? super Tuple<K, V>> predicate) {
        return decorated.filter(predicate);
    }

    @Override
    public <R> Stream<R> map(Function<? super Tuple<K, V>, ? extends R> mapper) {
        return decorated.map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Tuple<K, V>> mapper) {
        return decorated.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Tuple<K, V>> mapper) {
        return decorated.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Tuple<K, V>> mapper) {
        return decorated.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Tuple<K, V>, ? extends Stream<? extends R>> mapper) {
        return decorated.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Tuple<K, V>, ? extends IntStream> mapper) {
        return decorated.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Tuple<K, V>, ? extends LongStream> mapper) {
        return decorated.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Tuple<K, V>, ? extends DoubleStream> mapper) {
        return decorated.flatMapToDouble(mapper);
    }

    @Override
    public Stream<Tuple<K, V>> distinct() {
        return decorated.distinct();
    }

    @Override
    public Stream<Tuple<K, V>> sorted() {
        return decorated.sorted();
    }

    @Override
    public Stream<Tuple<K, V>> sorted(Comparator<? super Tuple<K, V>> comparator) {
        return decorated.sorted(comparator);
    }

    @Override
    public Stream<Tuple<K, V>> peek(Consumer<? super Tuple<K, V>> action) {
        return decorated.peek(action);
    }

    @Override
    public Stream<Tuple<K, V>> limit(long maxSize) {
        return decorated.limit(maxSize);
    }

    @Override
    public Stream<Tuple<K, V>> skip(long n) {
        return decorated.skip(n);
    }

    @Override
    public void forEach(Consumer<? super Tuple<K, V>> action) {
        decorated.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Tuple<K, V>> action) {
        decorated.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return decorated.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return decorated.toArray(generator);
    }

    @Override
    public Tuple<K, V> reduce(Tuple<K, V> identity, BinaryOperator<Tuple<K, V>> accumulator) {
        return decorated.reduce(identity, accumulator);
    }

    @Override
    public Optional<Tuple<K, V>> reduce(BinaryOperator<Tuple<K, V>> accumulator) {
        return decorated.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Tuple<K, V>, U> accumulator, BinaryOperator<U> combiner) {
        return decorated.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Tuple<K, V>> accumulator, BiConsumer<R, R> combiner) {
        return decorated.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Tuple<K, V>, A, R> collector) {
        return decorated.collect(collector);
    }

    @Override
    public Optional<Tuple<K, V>> min(Comparator<? super Tuple<K, V>> comparator) {
        return decorated.min(comparator);
    }

    @Override
    public Optional<Tuple<K, V>> max(Comparator<? super Tuple<K, V>> comparator) {
        return decorated.max(comparator);
    }

    @Override
    public long count() {
        return decorated.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Tuple<K, V>> predicate) {
        return decorated.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Tuple<K, V>> predicate) {
        return decorated.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Tuple<K, V>> predicate) {
        return decorated.noneMatch(predicate);
    }

    @Override
    public Optional<Tuple<K, V>> findFirst() {
        return decorated.findFirst();
    }

    @Override
    public Optional<Tuple<K, V>> findAny() {
        return decorated.findAny();
    }

    @Override
    public Iterator<Tuple<K, V>> iterator() {
        return decorated.iterator();
    }

    @Override
    public Spliterator<Tuple<K, V>> spliterator() {
        return decorated.spliterator();
    }

    @Override
    public boolean isParallel() {
        return decorated.isParallel();
    }

    @Override
    public Stream<Tuple<K, V>> sequential() {
        return decorated.sequential();
    }

    @Override
    public Stream<Tuple<K, V>> parallel() {
        return decorated.parallel();
    }

    @Override
    public Stream<Tuple<K, V>> unordered() {
        return decorated.unordered();
    }

    @Override
    public Stream<Tuple<K, V>> onClose(Runnable closeHandler) {
        return decorated.onClose(closeHandler);
    }

    @Override
    public void close() {
        decorated.close();
    }
}
