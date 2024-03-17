package com.noCompany.BackendStableDiffusionWebApp.dto.huggingface;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InferenceRequestToHuggingFace {

    private String inputs;
    private Options options;

    public InferenceRequestToHuggingFace(String inputs) {
        this.inputs = inputs;
        this.options = new Options(false, true);
    }

}

record Options(Boolean use_cache, Boolean wait_for_model){}
