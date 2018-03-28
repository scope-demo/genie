/*
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.common.dto.v4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * Fields representing all the values users can set when creating a new Command resource.
 *
 * @author tgianos
 * @since 4.0.0
 */
@Getter
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
@ToString(callSuper = true, doNotUseGetters = true)
@JsonDeserialize(builder = CommandRequest.Builder.class)
@SuppressWarnings("checkstyle:finalclass")
public class CommandRequest extends CommonRequestImpl {

    @Valid
    private final CommandMetadata metadata;
    @NotEmpty(message = "At least one executable entry is required")
    private final ImmutableList<@Size(max = 255, message = "Executable elements can only be 255 characters") String>
        executable;
    @Min(
        value = 1,
        message = "The minimum amount of memory if desired is 1 MB. Probably should be much more than that"
    )
    private final Integer memory;

    private CommandRequest(final Builder builder) {
        super(builder);
        this.metadata = builder.bMetadata;
        this.executable = builder.bExecutable;
        this.memory = builder.bMemory;
    }

    /**
     * Get the default amount of memory (in MB) to use for jobs which use this command.
     *
     * @return {@link Optional} wrapper of the amount of memory to use for a job
     */
    public Optional<Integer> getMemory() {
        return Optional.ofNullable(this.memory);
    }

    /**
     * Get the executable elements (generally a binary followed optionally by default arguments) that should be used
     * with this command when executing a job.
     *
     * @return The executable arguments as an immutable list. Any attempt to modify will throw an exception
     */
    public List<String> getExecutable() {
        return this.executable;
    }

    /**
     * Builder for a V4 Command Request.
     *
     * @author tgianos
     * @since 4.0.0
     */
    public static class Builder extends CommonRequestImpl.Builder<Builder> {

        private final CommandMetadata bMetadata;
        private final ImmutableList<String> bExecutable;
        private Integer bMemory;

        /**
         * Constructor which has required fields.
         *
         * @param metadata   The user supplied metadata about a command resource
         * @param executable The executable arguments to use on job process launch. Typically the binary path followed
         *                   by optional default parameters for that given binary. Must have at least one
         */
        @JsonCreator
        public Builder(
            @JsonProperty(value = "metadata", required = true) final CommandMetadata metadata,
            @JsonProperty(value = "executable", required = true) final List<String> executable
        ) {
            super();
            this.bMetadata = metadata;
            this.bExecutable = ImmutableList.copyOf(executable);
        }

        /**
         * Set the amount of memory (in MB) to default jobs run with this command to use.
         *
         * @param memory The default amount of memory (in MB) for jobs to use
         * @return The builder
         */
        public Builder withMemory(@Nullable final Integer memory) {
            this.bMemory = memory;
            return this;
        }

        /**
         * Build a new CommandRequest instance.
         *
         * @return The immutable command request
         */
        public CommandRequest build() {
            return new CommandRequest(this);
        }
    }
}