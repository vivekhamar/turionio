package com.acrylplatform.api.http

import javax.ws.rs.Path
import akka.http.scaladsl.server.Route
import com.acrylplatform.settings.RestAPISettings
import com.acrylplatform.utx.UtxPool
import io.netty.channel.group.ChannelGroup
import io.swagger.annotations._
import com.acrylplatform.api.http.assets.TransferV1Request
import com.acrylplatform.http.BroadcastRoute
import com.acrylplatform.utils.Time
import com.acrylplatform.transaction.TransactionFactory
import com.acrylplatform.wallet.Wallet

@Path("/payment")
@Api(value = "/payment")
@Deprecated
case class PaymentApiRoute(settings: RestAPISettings, wallet: Wallet, utx: UtxPool, allChannels: ChannelGroup, time: Time)
    extends ApiRoute
    with WithSettings
    with BroadcastRoute {

  override lazy val route = payment

  @Deprecated
  @ApiOperation(
    value = "Send payment. Deprecated: use /assets/transfer instead",
    notes = "Send payment to another wallet. Deprecated: use /assets/transfer instead",
    httpMethod = "POST",
    produces = "application/json",
    consumes = "application/json"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Json with data",
        required = true,
        paramType = "body",
        dataType = "com.acrylplatform.api.http.assets.TransferV1Request",
        defaultValue = "{\n\t\"amount\":400,\n\t\"fee\":1,\n\t\"sender\":\"senderId\",\n\t\"recipient\":\"recipientId\"\n}"
      )
    ))
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Json with response or error")
    ))
  def payment: Route = (path("payment") & post & withAuth) {
    json[TransferV1Request] { p =>
      doBroadcast(TransactionFactory.transferAssetV1(p, wallet, time))
    }
  }
}
