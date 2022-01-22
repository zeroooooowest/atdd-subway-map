package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // given
        var params = new HashMap<String, String>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        var response = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        var params1 = new HashMap<String, String>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        var createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        var params2 = new HashMap<String, String>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");

        var createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        var response = RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var lineNames = response.jsonPath().getList("name");
        assertThat(lineNames).contains(params1.get("name"), params2.get("name"));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        var params = new HashMap<String, String>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        var createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        var uri = createResponse.header("Location");
        var response = RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        var params = new HashMap<String, String>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        var createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        var modifyParams = new HashMap<String, String>();
        modifyParams.put("color", "bg-blue-600");
        modifyParams.put("name", "구분당선");

        var uri = createResponse.header("Location");
        var response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(modifyParams)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var lineName = response.jsonPath().getString("name");
        assertThat(lineName).isEqualTo(modifyParams.get("name"));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        var params = new HashMap<String, String>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        var createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        var uri = createResponse.header("Location");
        var response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 같은 이름으로 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 실패한다.
     */
    @DisplayName("중복 이름으로 지하철 노선 생성")
    @Test
    void createLineWithDuplicateName() {
        // given
        var params = new HashMap<String, String>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        var createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        var response = RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
