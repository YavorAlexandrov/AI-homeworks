import java.util.HashMap;
import java.util.Map;

public class TreeNode {
    private String feature;
    private final boolean isLeafNode;
    private final String mostFrequentClass;
    private final Map<String, TreeNode> children;

    public TreeNode(String feature, boolean isLeafNode, String mostFrequentClass) {
        this.feature = feature;
        this.isLeafNode = isLeafNode;
        this.mostFrequentClass = mostFrequentClass;
        this.children = new HashMap<>();
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void addChildNode(String featureValue, TreeNode child) {
        this.children.put(featureValue, child);
    }

    public String getMostFrequentClass() {
        return this.mostFrequentClass;
    }

    public boolean getIsLeafNode() {
        return this.isLeafNode;
    }

    public String getFeature() {
        return this.feature;
    }

    public Map<String, TreeNode> getChildren() {
        return this.children;
    }
}
