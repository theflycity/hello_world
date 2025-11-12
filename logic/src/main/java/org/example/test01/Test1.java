package org.example.test01;

public class Test1 {
    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        Test1 test1 = new Test1();
        int[] result = test1.twoSum2(nums, target);
        System.out.println(result[0] + " " + result[1]);
    }
    public int[] twoSum(int[] nums, int target) {
        // 对于输入的数组，使用两层循环，时间复杂度O(n²)
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }
    public int[] twoSum2(int[] nums, int target) {
        // 使用一个HashMap，时间复杂度O(n)
        java.util.HashMap<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}
