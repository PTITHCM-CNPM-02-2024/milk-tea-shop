package com.mts.backend.application.report;

import com.mts.backend.application.report.query.BasicReportQuery;
import com.mts.backend.application.report.query.CatRevenueReportQuery;
import com.mts.backend.application.report.query.OrderRevenueByTimeQuery;
import com.mts.backend.application.report.query.TopSaleProductQuery;
import com.mts.backend.application.report.query_handler.GetCatRevenueReportQueryHandler;
import com.mts.backend.application.report.query_handler.GetOverviewReportQueryHandler;
import com.mts.backend.application.report.query_handler.GetOrderRevenueByTimeQueryHandler;
import com.mts.backend.application.report.query_handler.GetTopSaleProductQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class ReportQueryBus extends AbstractQueryBus {
    public ReportQueryBus(GetOverviewReportQueryHandler getOverviewReportQueryHandler,
                          GetCatRevenueReportQueryHandler getProductRevenueByMonthReportQueryHandler,
                          GetOrderRevenueByTimeQueryHandler getRevenueOrderByTimeQueryHandler,
                          GetTopSaleProductQueryHandler getTopSaleProductQueryHandler) {
        register(BasicReportQuery.class, getOverviewReportQueryHandler);
        register(CatRevenueReportQuery.class, getProductRevenueByMonthReportQueryHandler);
        register(OrderRevenueByTimeQuery.class, getRevenueOrderByTimeQueryHandler);
        register(TopSaleProductQuery.class, getTopSaleProductQueryHandler);
    }
}
