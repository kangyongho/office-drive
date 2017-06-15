package net.ddns.office.drive.helper;

/**
 * Created by NPOST on 2017-06-13.
 * 페이징 핵심: 페이지 수
 * 페이징이란 아래 1, 2, 3 ... 등 페이지 번호를 생성하는 로직이다.
 */
public class Pagination {

    private int totalListCount; //총 게시물 수 (DB에서 구해온다)
    private int countList;      //한 페이지에 출력될 게시물 수
    private int countPage;      //한 화면에 출력될 페이지 수 (아래 페이징 번호)
    private int page;           //현재 페이지 번호 (html에서 a태그로 입력 받는다)

    private int totalPage;      //총 페이지 수: 로직으로 구한다.
    private int startpage;      //시작 페이지: 로직으로 구해야 한다.
    private int endPage;        //끝 페이지: 로직으로 구한다.

    public Pagination() {
    }

    public Pagination(int totalListCount, int countList, int countPage, int page) {
        this.totalListCount = totalListCount;
        this.countList = countList;
        this.countPage = countPage;
        this.page = page;
    }

    public int getTotalListCount() {
        return totalListCount;
    }

    public void setTotalListCount(int totalListCount) {
        this.totalListCount = totalListCount;
    }

    public int getCountList() {
        return countList;
    }

    public void setCountList(int countList) {
        this.countList = countList;
    }

    public int getCountPage() {
        return countPage;
    }

    public void setCountPage(int countPage) {
        this.countPage = countPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Pagination 핵심로직
     * 총 페이지 수
     * @return totalPage
     */
    public int getTotalPage() {
        totalPage = totalListCount / countList;          //총 페이지 수
        if (totalListCount % countList > 0) totalPage++; //나머지가 0이 아니면 페이지를 하나 늘려준다. 아직 글이 남아있다.
        if (totalPage < page) page = totalPage;          //현재 페이지가 총 페이지를 넘지 않도록 한다.
        return totalPage;
    }

    /**
     * 시작 페이지
     * Pagination 블록의 시작을 지정한다.
     * <a>태그의 시작페이지를 그대로 page에 입력하면 블록의 시작값이 매번 변경된다.
     * @return startPage
     */
    public int getStartPage() {
        startpage = ((page - 1) / countPage) * countPage + 1;
        return startpage;
    }

    /**
     * 끝 페이지
     * 총 페이지를 넘어가지 않도록 한다.
     * @return endPage
     */
    public int getEndPage() {
        endPage = startpage + countPage - 1;
        if (endPage > totalPage) endPage = totalPage;
        return endPage;
    }
}
