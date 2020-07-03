package morse.signal.clustering;

import morse.utils.statistics.Range;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JenksNaturalBreaksClusteringTest {
    private JenksNaturalBreaksClustering clustering = new JenksNaturalBreaksClustering();

    @Test
    public void createClustersFromSamples() {
        List<Range<Integer>> clusters = clustering.getClusters(List.of(1, 8, 2, 9, 3, 7, 1, 8, 3, 9, 2, 8));
        assertEquals(List.of(new Range<>(1, 3), new Range<>(7, 9)), clusters);
    }
}
