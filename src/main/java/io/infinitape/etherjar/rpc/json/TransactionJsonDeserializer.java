package io.infinitape.etherjar.rpc.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.infinitape.etherjar.model.ChainId;
import io.infinitape.etherjar.model.HexQuantity;
import io.infinitape.etherjar.model.TransactionSignature;

import java.io.IOException;

/**
 * @author Igor Artamonov
 */
public class TransactionJsonDeserializer extends EtherJsonDeserializer<TransactionJson> {

    @Override
    public TransactionJson deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.readValueAsTree();
        return deserialize(node);
    }

    public TransactionJson deserialize(JsonNode node) {
        TransactionJson tx = new TransactionJson();
        tx.setHash(getTxHash(node, "hash"));
        tx.setNonce(getQuantity(node, "nonce").getValue().longValue());
        tx.setBlockHash(getBlockHash(node, "blockHash"));
        HexQuantity blockNumber = getQuantity(node, "blockNumber");
        if (blockNumber != null)  {
            tx.setBlockNumber(blockNumber.getValue().longValue());
        }
        HexQuantity txIndex = getQuantity(node, "transactionIndex");
        if (txIndex != null) {
            tx.setTransactionIndex(txIndex.getValue().longValue());
        }
        tx.setFrom(getAddress(node, "from"));
        tx.setTo(getAddress(node, "to"));
        tx.setValue(getWei(node, "value"));
        tx.setGasPrice(getWei(node, "gasPrice"));
        tx.setGas(getQuantity(node, "gas"));
        tx.setInput(getData(node, "input"));

        if (node.has("r") && node.has("v") && node.has("s")) {
            TransactionSignature signature = new TransactionSignature();

            if (node.hasNonNull("networkId")) {
                signature.setChainId(new ChainId(node.get("networkId").intValue()));
            }
            signature.setR(getData(node, "r"));
            signature.setS(getData(node, "s"));
            signature.setV(getLong(node, "v").intValue());
            signature.setPublicKey(getData(node, "publicKey"));

            tx.setSignature(signature);
        }

        return tx;
    }
}
