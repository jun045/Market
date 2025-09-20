package project.market;

public record PageInfo(int pageNumber,
                       int size,
                       long totalElement,
                       int totalPage) {
}
