package com.acrylplatform.lagonaki.mocks

import com.acrylplatform.account.KeyPair
import com.acrylplatform.block._
import com.acrylplatform.common.state.ByteStr
import com.acrylplatform.common.utils.EitherExt2
import com.acrylplatform.consensus.nxt.NxtLikeConsensusBlockData
import com.acrylplatform.crypto._
import com.acrylplatform.transaction.Transaction

import scala.util.{Random, Try}

object TestBlock {
  val defaultSigner = KeyPair(Array.fill(KeyLength)(0: Byte))

  val random: Random = new Random()

  def randomOfLength(length: Int): ByteStr = ByteStr(Array.fill(length)(random.nextInt().toByte))

  def randomSignature(): ByteStr = randomOfLength(SignatureLength)

  def sign(signer: KeyPair, b: Block): Block = {
    Block
      .buildAndSign(
        version = b.version,
        timestamp = b.timestamp,
        reference = b.reference,
        consensusData = b.consensusData,
        transactionData = b.transactionData,
        signer = signer,
        featureVotes = b.featureVotes
      )
      .explicitGet()
  }

  def create(txs: Seq[Transaction]): Block = create(defaultSigner, txs)

  def create(signer: KeyPair, txs: Seq[Transaction]): Block =
    create(time = Try(txs.map(_.timestamp).max).getOrElse(0), txs = txs, signer = signer)

  def create(signer: KeyPair, txs: Seq[Transaction], features: Set[Short]): Block =
    create(time = Try(txs.map(_.timestamp).max).getOrElse(0), ref = randomSignature(), txs = txs, signer = signer, version = 3, features = features)

  def create(time: Long, txs: Seq[Transaction]): Block = create(time, randomSignature(), txs, defaultSigner)

  def create(time: Long, txs: Seq[Transaction], signer: KeyPair): Block = create(time, randomSignature(), txs, signer)

  def create(time: Long,
             ref: ByteStr,
             txs: Seq[Transaction],
             signer: KeyPair = defaultSigner,
             version: Byte = 2,
             features: Set[Short] = Set.empty[Short]): Block =
    sign(
      signer,
      Block(
        timestamp = time,
        version = version,
        reference = ref,
        signerData = SignerData(signer, ByteStr.empty),
        consensusData = NxtLikeConsensusBlockData(2L, ByteStr(Array.fill(Block.GeneratorSignatureLength)(0: Byte))),
        transactionData = txs,
        featureVotes = features
      )
    )

  def withReference(ref: ByteStr): Block =
    sign(
      defaultSigner,
      Block(0,
            1,
            ref,
            SignerData(defaultSigner, ByteStr.empty),
            NxtLikeConsensusBlockData(2L, randomOfLength(Block.GeneratorSignatureLength)),
            Seq.empty,
            Set.empty)
    )

  def withReferenceAndFeatures(ref: ByteStr, features: Set[Short]): Block =
    sign(
      defaultSigner,
      Block(0,
            3,
            ref,
            SignerData(defaultSigner, ByteStr.empty),
            NxtLikeConsensusBlockData(2L, randomOfLength(Block.GeneratorSignatureLength)),
            Seq.empty,
            features)
    )
}
