package org.hatdex.hat.utils

import io.dataswift.adjudicator.Types.{ Contract, ContractId }
import play.api.libs.ws.{ WSClient, WSRequest, WSResponse }

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.Future

// TODO: rename to adjudicator
// TODO: details from config
object NetworkRequest {

  //get the URL from config
  def getPublicKey(
    adjudicatorEndpoint: String,
    contractId: ContractId,
    hatName: String,
    keyId: String,
    ws: WSClient)(implicit ec: ExecutionContext): Future[WSResponse] = {
    // TODO: update this endpoint
    val url =
      s"${adjudicatorEndpoint}/v1/contracts/${contractId}/hat/${hatName}/${keyId}"
    val req = makeRequest(url, ws)
    req.get()
  }

  def joinContract(
    adjudicatorEndpoint: String,
    hatName: String,
    contractId: ContractId,
    ws: WSClient)(implicit ec: ExecutionContext): Future[WSResponse] = {
    val url =
      s"${adjudicatorEndpoint}/v1/contracts/${contractId}/hat/${hatName}"
    val req =
      makeRequest(url, ws).withHttpHeaders("Content-Type" -> "application/json")
    req.put("d")
  }

  def leaveContract(
    adjudicatorEndpoint: String,
    hatName: String,
    contractId: ContractId,
    ws: WSClient)(implicit ec: ExecutionContext): Future[WSResponse] = {
    val url =
      s"${adjudicatorEndpoint}/v1/contracts/${contractId}/hat/${hatName}"
    val req = makeRequest(url, ws)
    req.delete()
  }

  private def makeRequest(url: String, ws: WSClient)(
    implicit
    ec: ExecutionContext): WSRequest = {
    ws.url(url)
    // TODO: Auth to ADJ goes here.
    //.withHttpHeaders("Accept" -> "application/json"
    //, "X-Auth-Token" -> hatSharedSecret)
    // )
    //request.get()
  }
}
