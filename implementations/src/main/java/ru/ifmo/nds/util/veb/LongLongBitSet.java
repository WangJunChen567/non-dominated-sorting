package ru.ifmo.nds.util.veb;

final class LongLongBitSet extends VanEmdeBoasSet {
    private static final int limit = 1 << 12;
    private final long[] clusters;
    private long summary;

    private int min, max;

    LongLongBitSet() {
        min = limit;
        max = -1;
        clusters = new long[64];
        summary = 0;
    }

    @Override
    public boolean isEmpty() {
        return max == -1;
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public int prev(int index) {
        if (index > max) {
            return max;
        }
        if (index <= min) {
            return -1;
        }
        int h = hi(index);
        long chs = (clusters[h] << ~index) << 1;
        if (chs == 0) {
            h = VanEmdeBoasSet.prev(summary, h);
            return h < 0 ? min : join(h, VanEmdeBoasSet.max(clusters[h]));
        } else {
            return index - 1 - Long.numberOfLeadingZeros(chs);
        }
    }

    @Override
    public int prevInclusively(int index) {
        if (index <= min) {
            // same as "index == min ? min : -1"
            return min | ((index - min) >> 31);
        }
        if (index >= max) {
            return max;
        }
        int h = hi(index);
        long chs = clusters[h] << ~index;
        if (chs == 0) {
            h = VanEmdeBoasSet.prev(summary, h);
            return h < 0 ? min : join(h, VanEmdeBoasSet.max(clusters[h]));
        } else {
            return index - Long.numberOfLeadingZeros(chs);
        }
    }


    @Override
    public int next(int index) {
        if (index < min) {
            return min;
        }
        if (index >= max) {
            return limit;
        }
        int h = hi(index);
        long chs = (clusters[h] >>> index) >>> 1;
        if (chs == 0) {
            h = VanEmdeBoasSet.next(summary, h);
            return h >= 64 ? max : join(h, VanEmdeBoasSet.min(clusters[h]));
        } else {
            return index + 1 + Long.numberOfTrailingZeros(chs);
        }
    }

    @Override
    public void add(int index) {
        if (max < 0) {
            min = max = index;
        } else if (min == max && index != min) {
            if (index < min) {
                min = index;
            } else {
                max = index;
            }
        } else if (index != min && index != max) {
            if (index < min) {
                int tmp = min;
                min = index;
                index = tmp;
            }
            if (index > max) {
                int tmp = max;
                max = index;
                index = tmp;
            }
            int h = hi(index);
            if (clusters[h] == 0) {
                summary |= 1L << h;
            }
            clusters[h] |= 1L << index;
        }
    }

    @Override
    public void remove(int index) {
        if (index == min) {
            if (index == max) {
                min = limit;
                max = -1;
            } else {
                int newMin = next(min);
                if (newMin != max) {
                    remove(newMin);
                }
                min = newMin;
            }
        } else if (index == max) {
            int newMax = prev(max);
            if (newMax != min) {
                remove(newMax);
            }
            max = newMax;
        } else if (min < index && index < max) {
            int h = hi(index);
            clusters[h] &= ~(1L << index);
            if (clusters[h] == 0) {
                summary &= ~(1L << h);
            }
        }
    }

    @Override
    public void clear() {
        min = limit;
        max = -1;
        for (int i = VanEmdeBoasSet.min(summary); i < 64; i = VanEmdeBoasSet.next(summary, i)) {
            clusters[i] = 0;
        }
        summary = 0;
    }

    private boolean cleanupMidMax(int from, int offset, int value, int[] values) {
        if (summary != 0) {
            for (int i = from == -1 ? VanEmdeBoasSet.min(summary) : VanEmdeBoasSet.next(summary, from);
                 i < clusters.length;
                 i = VanEmdeBoasSet.next(summary, i)) {
                clusters[i] = VanEmdeBoasSet.cleanupUpwards(clusters[i], offset + (i << 6), value, values);
                if (clusters[i] == 0) {
                    summary ^= 1L << i;
                } else {
                    return false;
                }
            }
        }
        return values[offset + max] <= value;
    }

    @Override
    public void setEnsuringMonotonicity(int index, int offset, int value, int[] values) {
        if (min == max) {
            // Only one element is stored. Consider where we fall...
            if (index < min) {
                values[offset + index] = value;
                min = index;
                if (values[offset + max] <= value) {
                    max = index;
                }
            } else if (index == min) {
                int oi = offset + index;
                if (values[oi] < value) {
                    values[oi] = value;
                }
            } else {
                if (values[offset + min] < value) {
                    values[offset + index] = value;
                    max = index;
                }
            }
        } else if (max == -1) {
            // The set was empty. Just set the value.
            min = index;
            max = index;
            values[offset + index] = value;
        } else {
            if (index < min) {
                // First of all, insert ourselves.
                values[offset + index] = value;
                int oldMin = min;
                min = index;

                if (values[offset + oldMin] > value) {
                    // Add the old minimum and break out.
                    int h = hi(oldMin);
                    clusters[h] |= 1L << oldMin;
                    summary |= 1L << h;
                } else {
                    // Do not add the old minimum, as it is dominated.
                    if (cleanupMidMax(-1, offset, value, values)) {
                        max = min;
                    }
                }
            } else if (index == min) {
                int idx = offset + index;
                if (values[idx] < value) {
                    // Replace the value at the minimum, clean up the tail.
                    values[idx] = value;
                    if (cleanupMidMax(-1, offset, value, values)) {
                        max = min;
                    }
                }
            } else if (max <= index) {
                // We either replace max or add a new index after max
                if (values[offset + max] < value) {
                    values[offset + index] = value;
                    if (max != index) {
                        int oldMax = max;
                        max = index;
                        add(oldMax);
                    }
                }
            } else if (summary == 0) {
                if (values[offset + min] < value) {
                    values[offset + index] = value;
                    if (values[offset + max] > value) {
                        // Normal insertion
                        int h = hi(index);
                        clusters[h] |= 1L << index;
                        summary |= 1L << h;
                    } else {
                        // Replacement of max
                        max = index;
                    }
                }
            } else {
                int h = hi(index);
                long greaterThanCurrentMask = (-1L << index) << 1;
                long upTo = clusters[h] & ~greaterThanCurrentMask;
                if (upTo == 0) {
                    int hPrev = VanEmdeBoasSet.prev(summary, h);
                    int iPrev = hPrev == -1 ? min : join(hPrev, VanEmdeBoasSet.max(clusters[hPrev]));
                    if (values[offset + iPrev] >= value) {
                        return;
                    }
                } else {
                    int lPrev = 63 - Long.numberOfLeadingZeros(upTo);
                    if (values[offset + (h << 6) + lPrev] >= value) {
                        return;
                    }
                }
                summary |= 1L << h;
                clusters[h] = VanEmdeBoasSet.setEnsuringMonotonicity(clusters[h], index & 63, offset + (h << 6), value, values);
                values[offset + index] = value;
                if ((clusters[h] & greaterThanCurrentMask) == 0) {
                    if (cleanupMidMax(h, offset, value, values)) {
                        max = index;
                        clusters[h] ^= 1L << index;
                        if (clusters[h] == 0) {
                            summary ^= 1L << h;
                        }
                    }
                }
            }
        }
    }

    @Override
    void cleanupUpwards(int offset, int value, int[] values) {
        if (values[offset + max] <= value) {
            clear();
        } else if (values[offset + min] <= value) {
            if (summary != 0) {
                // need to cleanup at least something
                for (int i = VanEmdeBoasSet.min(summary); i < clusters.length; i = VanEmdeBoasSet.next(summary, i)) {
                    clusters[i] = VanEmdeBoasSet.cleanupUpwards(clusters[i], offset + (i << 6), value, values);
                    if (clusters[i] != 0) {
                        int min = VanEmdeBoasSet.min(clusters[i]);
                        this.min = min + (i << 6);
                        clusters[i] ^= 1L << min;
                        if (clusters[i] == 0) {
                            summary ^= 1L << i;
                        }
                        return;
                    }
                    summary ^= 1L << i;
                }
            }
            // summary == 0 here
            min = max;
        }
    }

    private static int hi(int index) {
        return index >>> 6;
    }
    private static int join(int hi, int lo) {
        return (hi << 6) ^ lo;
    }
}
