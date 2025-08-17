package org.karthick.socialpostagent;

import org.karthick.socialpostagent.entity.LLMChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LLMChatLogRepository extends MongoRepository<LLMChatLog, String> {}
